package com.networknt.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyRuleContractCompatibilityTest {
    private static final String LEGACY_FIXTURE = "rules-2.0.1-compat.yml";

    @Test
    void exactTwoPointZeroPointOneFixtureDeserializesWithoutLoss() throws Exception {
        Map<String, Rule> rules = readRules(LEGACY_FIXTURE);

        assertEquals(53, rules.size());

        Rule firstRule = rules.get("rule1");
        assertNotNull(firstRule);
        assertEquals("rule1", firstRule.getRuleId());
        RuleCondition firstCondition = firstRule.getConditions().iterator().next();
        assertEquals("ClassA.Bobject.Cobject.Cint", firstCondition.getPropertyPath());
        assertEquals("equals", firstCondition.getOperatorCode());
        RuleConditionValue firstValue = firstCondition.getConditionValues().iterator().next();
        assertEquals("cv1", firstValue.getConditionValueId());
        assertEquals("6", firstValue.getConditionValue());
        assertEquals("STRING", firstValue.getValueTypeCode());
        assertEquals("com.networknt.rule.ValidationAction",
                actionClassName(firstRule.getActions().iterator().next()));

        RuleConditionValue dateValue = conditionValue(rules, "test-on-date-format-rule");
        assertEquals("cv1", dateValue.getConditionValueId());
        assertEquals("yyyy-MM-dd HH:mm:ss", dateValue.getDateFormat());

        RuleConditionValue regexValue = conditionValue(rules, "test-match-rule-flags");
        assertEquals("cv1", regexValue.getConditionValueId());
        assertEquals("i", regexValue.getRegexFlags());

        Rule expressionRule = rules.get("role-based-auth-skip-cc");
        assertEquals("(allow-cc OR allow-role)", expressionRule.getConditionExpression());
        assertEquals(2, expressionRule.getConditions().size());
        RuleActionValue actionValue = expressionRule.getActions().iterator().next()
                .getActionValues().iterator().next();
        assertEquals("roles", actionValue.getActionValueId());
        assertEquals("$roles", actionValue.getValue());

        Rule filteredRule = rules.get("get-config-filter");
        assertEquals("response-filter", filteredRule.getRuleType());
        assertEquals(2, filteredRule.getActions().size());
        assertTrue(filteredRule.getActions().stream().allMatch(RuleAction::isConditionResult));

        Set<String> ruleTypes = new LinkedHashSet<>();
        rules.values().forEach(rule -> ruleTypes.add(rule.getRuleType()));
        assertTrue(ruleTypes.contains("request-access"));
        assertTrue(ruleTypes.contains("response-transform"));
        assertTrue(ruleTypes.contains("response-filter"));
    }

    @Test
    void serializationUsesOnlyLegacyPropertyNames() throws Exception {
        Map<String, Rule> legacyRules = readRules(LEGACY_FIXTURE);
        Map<String, Rule> operatorRules = readRules("rules-legacy-operator-compat.yml");

        Rule representativeRule = operatorRules.get("concurrent-action-values");
        RuleCondition representativeCondition = representativeRule.getConditions().iterator().next();
        RuleAction representativeAction = representativeRule.getActions().iterator().next();
        RuleActionValue representativeActionValue = representativeAction.getActionValues().iterator().next();
        RuleConditionValue representativeConditionValue = conditionValue(legacyRules, "test-match-rule-flags");

        assertKeys(RuleMapper.yamlMapper.valueToTree(representativeRule),
                "ruleId", "ruleName", "ruleVersion", "hostId", "ruleType", "common",
                "ruleGroup", "ruleDesc", "ruleOwner", "conditions", "conditionExpression", "actions");
        assertKeys(RuleMapper.yamlMapper.valueToTree(representativeCondition),
                "conditionId", "conditionDesc", "propertyPath", "operatorCode", "index", "conditionValues");
        assertKeys(RuleMapper.yamlMapper.valueToTree(representativeConditionValue),
                "conditionValueId", "expression", "conditionValue", "valueTypeCode", "regexFlags", "dateFormat");
        assertKeys(RuleMapper.yamlMapper.valueToTree(representativeAction),
                "actionId", "actionDesc", "actionClassName", "conditionResult", "actionValues", "parameters");
        assertKeys(RuleMapper.yamlMapper.valueToTree(representativeActionValue),
                "actionValueId", "valueTypeCode", "value", "resolvedValue");
    }

    @Test
    void exactFixtureRoundTripsWithoutChangingTheObjectGraph() throws Exception {
        Map<String, Rule> original = readRules(LEGACY_FIXTURE);

        String serialized = RuleMapper.yamlMapper.writeValueAsString(original);
        Map<String, Rule> roundTripped = RuleMapper.string2RuleMap(serialized);

        RuleConditionValue originalDate = conditionValue(original, "test-on-date-format-rule");
        RuleConditionValue roundTrippedDate = conditionValue(roundTripped, "test-on-date-format-rule");
        RuleConditionValue originalRegex = conditionValue(original, "test-match-rule-flags");
        RuleConditionValue roundTrippedRegex = conditionValue(roundTripped, "test-match-rule-flags");

        assertAll(
                () -> assertEquals(originalDate.getConditionValueId(), roundTrippedDate.getConditionValueId()),
                () -> assertEquals(originalDate.getValueTypeCode(), roundTrippedDate.getValueTypeCode()),
                () -> assertEquals(originalDate.getDateFormat(), roundTrippedDate.getDateFormat()),
                () -> assertEquals(originalRegex.getConditionValueId(), roundTrippedRegex.getConditionValueId()),
                () -> assertEquals(originalRegex.getRegexFlags(), roundTrippedRegex.getRegexFlags()),
                () -> assertEquals(original, roundTripped));
    }

    @Test
    void celOnlyBodyIsRejected() {
        String yaml = "cel-rule:\n"
                + "  ruleId: cel-rule\n"
                + "  ruleType: request-access\n"
                + "  conditionLanguage: cel\n"
                + "  expression: request.path == '/admin'\n";

        assertUnknownPropertyRejected(yaml, "conditionLanguage");
    }

    @Test
    void mixedLegacyAndCelBodyIsRejected() {
        String yaml = "mixed-rule:\n"
                + "  ruleId: mixed-rule\n"
                + "  ruleType: request-access\n"
                + "  conditionLanguage: cel\n"
                + "  expression: request.path == '/admin'\n"
                + "  conditions:\n"
                + "    - conditionId: legacy-condition\n"
                + "      propertyPath: requestPath\n"
                + "      operatorCode: equals\n"
                + "      conditionValues:\n"
                + "        - conditionValue: /admin\n";

        assertUnknownPropertyRejected(yaml, "conditionLanguage");
    }

    @Test
    void compactConditionBodyIsRejected() {
        String yaml = "compact-rule:\n"
                + "  ruleId: compact-rule\n"
                + "  ruleType: request-access\n"
                + "  conditions:\n"
                + "    - conditionId: compact-condition\n"
                + "      operand: requestPath\n"
                + "      operator: equals\n"
                + "      expected: /admin\n";

        assertUnknownPropertyRejected(yaml, "operand");
    }

    @Test
    void ruleActionExposesOnlyTheLegacyClassNameAccessors() {
        assertDoesNotThrow(() -> RuleAction.class.getMethod("getActionClassName"));
        assertDoesNotThrow(() -> RuleAction.class.getMethod("setActionClassName", String.class));
        assertThrows(NoSuchMethodException.class, () -> RuleAction.class.getMethod("getActionRef"));
        assertThrows(NoSuchMethodException.class, () -> RuleAction.class.getMethod("setActionRef", String.class));
    }

    private static void assertUnknownPropertyRejected(String yaml, String propertyName) {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> RuleMapper.string2RuleMap(yaml));
        assertTrue(exception.getCause() instanceof UnrecognizedPropertyException,
                () -> "Expected UnrecognizedPropertyException but got " + exception.getCause());
        assertEquals(propertyName, ((UnrecognizedPropertyException) exception.getCause()).getPropertyName());
    }

    @Test
    void ruleActionRetainsTheLegacyPublicConstructor() {
        assertDoesNotThrow(() -> RuleAction.class.getConstructor(
                String.class,
                String.class,
                String.class,
                Boolean.class,
                Collection.class,
                Map.class));
    }

    @Test
    void actionInterfaceUsesTheAbstractCollectionContractOnly() throws Exception {
        Method performAction = IAction.class.getMethod(
                "performAction",
                String.class,
                String.class,
                Map.class,
                Map.class,
                Collection.class);

        assertTrue(Modifier.isAbstract(performAction.getModifiers()));
        assertFalse(performAction.isDefault());
        assertThrows(NoSuchMethodException.class, () -> IAction.class.getMethod(
                "performAction", String.class, String.class, Map.class, Map.class, Map.class));
        assertThrows(NoSuchMethodException.class, () -> IAction.class.getMethod(
                "postPerformAction", String.class, String.class, Map.class, Map.class, Map.class));
    }

    private static Map<String, Rule> readRules(String resourceName) throws IOException {
        return RuleMapper.string2RuleMap(readResource(resourceName));
    }

    private static String readResource(String resourceName) throws IOException {
        try (InputStream input = LegacyRuleContractCompatibilityTest.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            assertNotNull(input, "Missing test resource " + resourceName);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static RuleConditionValue conditionValue(Map<String, Rule> rules, String ruleId) {
        return rules.get(ruleId).getConditions().iterator().next().getConditionValues().iterator().next();
    }

    private static String actionClassName(RuleAction action) throws Exception {
        try {
            return (String) RuleAction.class.getMethod("getActionClassName").invoke(action);
        } catch (NoSuchMethodException ignored) {
            return (String) RuleAction.class.getMethod("getActionRef").invoke(action);
        }
    }

    private static void assertKeys(JsonNode node, String... expectedKeys) {
        Set<String> actual = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(actual::add);
        assertEquals(Set.of(expectedKeys), actual);
    }
}
