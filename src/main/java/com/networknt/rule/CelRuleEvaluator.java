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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

final class CelRuleEvaluator {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final String CONTAINS_IGNORE_CASE_OVERLOAD = "contains_ignore_case_string_string";
    private static final String CONTAINS_IGNORE_CASE_CAMEL_OVERLOAD = "containsIgnoreCase_string_string";
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

    static boolean evaluate(String ruleId, String expression, Map objMap) throws RuleEngineException {
        if (expression == null || expression.isBlank()) {
            throw new RuleEngineException("CEL expression is required when conditionLanguage is cel", ruleId);
        }

        Map<String, Object> normalizedContext = normalizeContext(objMap);
        Map<String, Object> activation = new HashMap<>();
        CelCompilerBuilder builder = CelCompilerFactory.standardCelCompilerBuilder()
                .setStandardMacros(CelStandardMacro.STANDARD_MACROS)
                .addFunctionDeclarations(CONTAINS_IGNORE_CASE_DECL, CONTAINS_IGNORE_CASE_CAMEL_DECL)
                .addVar("context", SimpleType.DYN);

        if (normalizedContext != null) {
            for (Object entryObject : normalizedContext.entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObject;
                if (entry.getKey() instanceof String key) {
                    activation.put(key, entry.getValue());
                    if (isIdentifier(key) && !"context".equals(key)) {
                        builder.addVar(key, SimpleType.DYN);
                    }
                }
            }
        }
        activation.put("context", normalizedContext == null ? Map.of() : normalizedContext);

        try {
            CelCompiler compiler = builder.build();
            CelAbstractSyntaxTree ast = compiler.compile(expression).getAst();
            Object result = CEL_RUNTIME.createProgram(ast).eval(activation);
            if (result instanceof Boolean bool) {
                return bool;
            }
            throw new RuleEngineException("CEL expression must return a boolean: " + result, ruleId);
        } catch (CelValidationException | CelEvaluationException e) {
            throw new RuleEngineException("Failed to evaluate CEL expression: " + e.getMessage(), ruleId, e);
        }
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
}
