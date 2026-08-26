package com.networknt.rule;

import com.networknt.rule.exception.ActionExecutionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyRuleBehaviorCompatibilityTest {

    @Test
    void retainedOperatorsAndAliasesExecuteThroughLegacyOperatorCode() throws Exception {
        Map<String, Rule> rules = readRules("rules-legacy-operator-compat.yml");
        RuleEngine engine = new RuleEngine(rules, null);
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Alpha");
        input.put("scopes", List.of("portal.r", "profile.r"));

        for (String ruleId : List.of(
                "alias-double-equals",
                "alias-eq",
                "alias-matches",
                "alias-exists",
                "alias-not-exists",
                "contains-any",
                "contains-all",
                "contains-none",
                "ends-with")) {
            Map<String, Object> result = engine.executeRule(ruleId, input);
            assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT), ruleId);
        }
    }

    @Test
    void defaultAndAndExplicitOrRemainBackwardCompatible() throws Exception {
        Map<String, Rule> rules = readRules("rules-legacy-operator-compat.yml");
        RuleEngine engine = new RuleEngine(rules, null);
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Alpha");
        input.put("scopes", List.of("portal.r"));

        Map<String, Object> defaultAnd = engine.executeRule("default-and", input);
        assertEquals(Boolean.TRUE, defaultAnd.get(RuleConstants.RESULT));
        assertEquals(Boolean.TRUE, defaultAnd.get("name-matches"));
        assertEquals(Boolean.TRUE, defaultAnd.get("scope-exists"));

        Map<String, Object> explicitOr = engine.executeRule("explicit-or", input);
        assertEquals(Boolean.TRUE, explicitOr.get(RuleConstants.RESULT));
        assertEquals(Boolean.FALSE, explicitOr.get("name-misses"));
        assertEquals(Boolean.TRUE, explicitOr.get("scope-exists"));
    }

    @Test
    void legacySingleAndConditionalMultipleActionsRetainRouting() throws Exception {
        Map<String, Rule> rules = readRules("rules-2.0.1-compat.yml");
        RuleEngine engine = new RuleEngine(rules, null);

        Map<String, Object> singleTrue = engine.executeRule(
                "test-single-action-true", new HashMap<>(Map.of("name", "ClassA")));
        assertEquals(Boolean.TRUE, singleTrue.get(RuleConstants.RESULT));
        assertEquals(Boolean.TRUE, singleTrue.get("MultipleActionOne"));

        Map<String, Object> singleFalse = engine.executeRule(
                "test-single-action-false", new HashMap<>(Map.of("name", "ClassA")));
        assertEquals(Boolean.FALSE, singleFalse.get(RuleConstants.RESULT));
        assertFalse(singleFalse.containsKey("MultipleActionOne"));

        Map<String, Object> classA = new HashMap<>();
        classA.put("Aname", "ClassA");
        Map<String, Object> multipleTrue = engine.executeRule(
                "test-multiple-actions", new HashMap<>(Map.of("ClassA", classA)));
        assertEquals(Boolean.TRUE, multipleTrue.get(RuleConstants.RESULT));
        assertEquals(Boolean.TRUE, multipleTrue.get("MultipleActionOne"));
        assertEquals(Boolean.TRUE, multipleTrue.get("MultipleActionThree"));
        assertFalse(multipleTrue.containsKey("MultipleActionTwo"));

        classA.put("Aname", null);
        Map<String, Object> multipleFalse = engine.executeRule(
                "test-multiple-actions", new HashMap<>(Map.of("ClassA", classA)));
        assertEquals(Boolean.FALSE, multipleFalse.get(RuleConstants.RESULT));
        assertEquals(Boolean.TRUE, multipleFalse.get("MultipleActionTwo"));
        assertEquals(Boolean.TRUE, multipleFalse.get("MultipleActionThree"));
        assertFalse(multipleFalse.containsKey("MultipleActionOne"));
    }

    @Test
    void concurrentActionResolutionDoesNotMutateTheSharedDefinition() throws Exception {
        Map<String, Rule> rules = readRules("rules-legacy-operator-compat.yml");
        Rule rule = rules.get("concurrent-action-values");
        Collection<RuleActionValue> definitionValues = rule.getActions().iterator().next().getActionValues();
        assertTrue(definitionValues.stream().allMatch(value -> value.getResolvedValue() == null));

        RuleEngine engine = new RuleEngine(rules, null);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<Map<String, Object>>> calls = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                String name = "name-" + i;
                calls.add(() -> engine.executeRule(
                        "concurrent-action-values", new HashMap<>(Map.of("name", name))));
            }

            List<Future<Map<String, Object>>> futures = executor.invokeAll(calls);
            for (int i = 0; i < futures.size(); i++) {
                Map<String, Object> result = futures.get(i).get();
                assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT));
                assertEquals("name-" + i, result.get("X-Test-Name"));
                assertEquals("fixed", result.get("X-Static"));
            }
        } finally {
            executor.shutdownNow();
        }

        for (RuleActionValue definitionValue : definitionValues) {
            assertNull(definitionValue.getResolvedValue(), definitionValue.getActionValueId());
        }
    }

    @Test
    void supplementalFixtureRetainsParametersMultipleValuesAndRequestTransformType() throws Exception {
        Rule rule = readRules("rules-legacy-operator-compat.yml").get("concurrent-action-values");
        assertEquals("request-transform", rule.getRuleType());

        RuleAction action = rule.getActions().iterator().next();
        assertEquals(2, action.getActionValues().size());
        assertEquals("replace", action.getParameters().get("headerMode"));
        assertEquals(Boolean.TRUE, action.getParameters().get("audit"));
    }

    @Test
    void missingActionClassNameProducesContextualActionExecutionException() {
        for (String actionClassName : new String[]{null, "   "}) {
            RuleAction action = new RuleAction();
            action.setActionId("missing-class-action");
            action.setActionClassName(actionClassName);

            Rule rule = new Rule();
            rule.setRuleId("missing-class-rule");
            rule.setActions(List.of(action));

            RuleEngine engine = new RuleEngine(Map.of(rule.getRuleId(), rule), null);
            ActionExecutionException exception = org.junit.jupiter.api.Assertions.assertThrows(
                    ActionExecutionException.class,
                    () -> engine.executeRule(rule.getRuleId(), new HashMap<>()));

            assertEquals("missing-class-rule", exception.getRuleId());
            assertEquals("missing-class-action", exception.getConditionId());
            assertTrue(exception.getMessage().contains("actionClassName is required"));
            assertTrue(exception.getMessage().contains("missing-class-rule"));
            assertTrue(exception.getMessage().contains("missing-class-action"));
        }
    }

    private static Map<String, Rule> readRules(String resourceName) throws IOException {
        try (InputStream input = LegacyRuleBehaviorCompatibilityTest.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            assertNotNull(input, "Missing test resource " + resourceName);
            return RuleMapper.string2RuleMap(new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
