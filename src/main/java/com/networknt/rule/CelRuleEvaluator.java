package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelFunctionDecl;
import dev.cel.common.CelOverloadDecl;
import dev.cel.common.CelValidationException;
import dev.cel.common.types.SimpleType;
import dev.cel.compiler.CelCompiler;
import dev.cel.compiler.CelCompilerBuilder;
import dev.cel.compiler.CelCompilerFactory;
import dev.cel.parser.CelStandardMacro;
import dev.cel.runtime.CelEvaluationException;
import dev.cel.runtime.CelFunctionBinding;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

final class CelRuleEvaluator {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final Pattern CONTEXT_REFERENCE = Pattern.compile("(^|[^A-Za-z0-9_])context([^A-Za-z0-9_]|$)");
    private static final Pattern MATCHES_REFERENCE = Pattern.compile("(^|[^A-Za-z0-9_])matches\\s*\\(|\\.\\s*matches\\s*\\(");
    private static final String CONDITION_SECURITY_PROFILE_STRICT = "strict";
    private static final String CONDITION_SECURITY_PROFILE_STANDARD = "standard";
    private static final String CONDITION_SECURITY_PROFILE_INTERNAL_ADMIN = "internal-admin";
    private static final Set<String> STRICT_ROOTS = Set.of(
            "auditInfo",
            "headers",
            "toolArguments",
            "endpoint",
            "toolName",
            "correlationId",
            "roles",
            "row",
            "col",
            "statusCode",
            "permission");
    private static final String CONTAINS_IGNORE_CASE_OVERLOAD = "contains_ignore_case_string_string";
    private static final String CONTAINS_IGNORE_CASE_CAMEL_OVERLOAD = "containsIgnoreCase_string_string";
    private static final ConcurrentMap<String, CelAbstractSyntaxTree> AST_CACHE = new ConcurrentHashMap<>();
    private static final CelFunctionDecl CONTAINS_IGNORE_CASE_DECL = CelFunctionDecl.newFunctionDeclaration(
            "contains_ignore_case",
            CelOverloadDecl.newGlobalOverload(CONTAINS_IGNORE_CASE_OVERLOAD, SimpleType.BOOL,
                    SimpleType.STRING, SimpleType.STRING));
    private static final CelFunctionDecl CONTAINS_IGNORE_CASE_CAMEL_DECL = CelFunctionDecl.newFunctionDeclaration(
            "containsIgnoreCase",
            CelOverloadDecl.newGlobalOverload(CONTAINS_IGNORE_CASE_CAMEL_OVERLOAD, SimpleType.BOOL,
                    SimpleType.STRING, SimpleType.STRING));
    private static final CelRuntime CEL_RUNTIME = CelRuntimeFactory.standardCelRuntimeBuilder()
            .addFunctionBindings(
                    CelFunctionBinding.from(CONTAINS_IGNORE_CASE_OVERLOAD, String.class, String.class,
                            CelRuleEvaluator::containsIgnoreCase),
                    CelFunctionBinding.from(CONTAINS_IGNORE_CASE_CAMEL_OVERLOAD, String.class, String.class,
                            CelRuleEvaluator::containsIgnoreCase))
            .build();

    private CelRuleEvaluator() {
    }

    static boolean evaluate(String ruleId, String expression, Map objMap, String conditionSecurityProfile, String ruleType) throws RuleEngineException {
        if (expression == null || expression.isBlank()) {
            throw new RuleEngineException("CEL expression is required when conditionLanguage is cel", ruleId);
        }

        CelSecurityProfile requestedProfile = CelSecurityProfile.requested(conditionSecurityProfile, ruleId);
        if (requestedProfile == CelSecurityProfile.INTERNAL_ADMIN) {
            throw new RuleEngineException("CEL conditionSecurityProfile internal-admin is disabled by runtime policy", ruleId);
        }
        CelSecurityProfile effectiveProfile = effectiveProfile(requestedProfile, ruleType);
        Map<String, Object> normalizedContext = normalizeContext(objMap);
        Map<String, Object> activation = new HashMap<>();
        Set<String> declaredVars = new LinkedHashSet<>();
        CelCompilerBuilder builder = CelCompilerFactory.standardCelCompilerBuilder()
                .setStandardMacros(CelStandardMacro.STANDARD_MACROS)
                .addFunctionDeclarations(CONTAINS_IGNORE_CASE_DECL, CONTAINS_IGNORE_CASE_CAMEL_DECL);

        if (effectiveProfile == CelSecurityProfile.STRICT) {
            validateStrictExpression(ruleId, expression);
            if (normalizedContext != null) {
                for (Map.Entry<String, Object> entry : normalizedContext.entrySet()) {
                    String key = entry.getKey();
                    if (isIdentifier(key) && STRICT_ROOTS.contains(key)) {
                        activation.put(key, entry.getValue());
                        addDeclaredVar(builder, declaredVars, key);
                    }
                }
            }
        } else {
            addDeclaredVar(builder, declaredVars, "context");
            if (normalizedContext != null) {
                for (Map.Entry<String, Object> entry : normalizedContext.entrySet()) {
                    String key = entry.getKey();
                    activation.put(key, entry.getValue());
                    if (isIdentifier(key) && !"context".equals(key)) {
                        addDeclaredVar(builder, declaredVars, key);
                    }
                }
            }
            activation.put("context", normalizedContext == null ? Map.of() : normalizedContext);
        }

        try {
            CelAbstractSyntaxTree ast = compileAst(ruleId, expression, effectiveProfile, declaredVars, builder);
            Object result = CEL_RUNTIME.createProgram(ast).eval(activation);
            if (result instanceof Boolean bool) {
                return bool;
            }
            throw new RuleEngineException("CEL expression must return a boolean: " + result, ruleId);
        } catch (CelEvaluationException e) {
            throw new RuleEngineException("Failed to evaluate CEL expression: " + e.getMessage(), ruleId, e);
        }
    }

    private static void addDeclaredVar(CelCompilerBuilder builder, Set<String> declaredVars, String name) {
        if (declaredVars.add(name)) {
            builder.addVar(name, SimpleType.DYN);
        }
    }

    private static CelAbstractSyntaxTree compileAst(String ruleId, String expression, CelSecurityProfile profile, Set<String> declaredVars, CelCompilerBuilder builder) throws RuleEngineException {
        String cacheKey = ruleId + ":" + profile.cacheName + ":" + String.join(",", declaredVars) + ":" + expression;
        CelAbstractSyntaxTree cached = AST_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        try {
            CelCompiler compiler = builder.build();
            CelAbstractSyntaxTree ast = compiler.compile(expression).getAst();
            CelAbstractSyntaxTree existing = AST_CACHE.putIfAbsent(cacheKey, ast);
            return existing == null ? ast : existing;
        } catch (CelValidationException e) {
            throw new RuleEngineException("Failed to evaluate CEL expression: " + e.getMessage(), ruleId, e);
        }
    }

    private static void validateStrictExpression(String ruleId, String expression) throws RuleEngineException {
        if (CONTEXT_REFERENCE.matcher(expression).find()) {
            throw new RuleEngineException("CEL strict profile does not expose full context; use curated root variables", ruleId);
        }
        if (MATCHES_REFERENCE.matcher(expression).find()) {
            throw new RuleEngineException("CEL strict profile does not allow regex matches", ruleId);
        }
    }

    private static CelSecurityProfile effectiveProfile(CelSecurityProfile requestedProfile, String ruleType) {
        return isResponsePhase(ruleType) ? CelSecurityProfile.STRICT : requestedProfile;
    }

    private static boolean isResponsePhase(String ruleType) {
        return "res-tra".equalsIgnoreCase(ruleType) || "res-fil".equalsIgnoreCase(ruleType);
    }

    private static boolean isIdentifier(String value) {
        return IDENTIFIER.matcher(value).matches()
                && !"true".equals(value)
                && !"false".equals(value)
                && !"null".equals(value)
                && !"in".equals(value);
    }

    private static Map<String, Object> normalizeContext(Map objMap) {
        if (objMap == null) {
            return null;
        }
        Map<String, Object> normalized = new LinkedHashMap<>();
        for (Object entryObject : objMap.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObject;
            if (entry.getKey() instanceof String key) {
                normalized.put(key, normalizeValue(entry.getValue()));
            }
        }
        return normalized;
    }

    private static Object normalizeValue(Object value) {
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return ((Number)value).longValue();
        }
        if (value instanceof Float) {
            return ((Number)value).doubleValue();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String key) {
                    normalized.put(key, normalizeValue(entry.getValue()));
                }
            }
            return normalized;
        }
        if (value instanceof List<?> list) {
            List<Object> normalized = new ArrayList<>(list.size());
            for (Object item : list) {
                normalized.add(normalizeValue(item));
            }
            return normalized;
        }
        return value;
    }

    private static boolean containsIgnoreCase(String actual, String expected) {
        return actual.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
    }

    private enum CelSecurityProfile {
        STRICT(CONDITION_SECURITY_PROFILE_STRICT),
        STANDARD(CONDITION_SECURITY_PROFILE_STANDARD),
        INTERNAL_ADMIN(CONDITION_SECURITY_PROFILE_INTERNAL_ADMIN);

        private final String cacheName;

        CelSecurityProfile(String cacheName) {
            this.cacheName = cacheName;
        }

        private static CelSecurityProfile requested(String value, String ruleId) throws RuleEngineException {
            String profile = value == null || value.isBlank()
                    ? CONDITION_SECURITY_PROFILE_STRICT
                    : value.trim().toLowerCase(Locale.ROOT);
            return switch (profile) {
                case CONDITION_SECURITY_PROFILE_STRICT -> STRICT;
                case CONDITION_SECURITY_PROFILE_STANDARD -> STANDARD;
                case CONDITION_SECURITY_PROFILE_INTERNAL_ADMIN -> INTERNAL_ADMIN;
                default -> throw new RuleEngineException("Unsupported conditionSecurityProfile " + profile, ruleId);
            };
        }
    }
}
