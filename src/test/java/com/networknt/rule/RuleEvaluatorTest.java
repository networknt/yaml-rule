package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class RuleEvaluatorTest {
    @Test
    public void testEvaluateConditionExpression() throws Exception {
        RuleEvaluator ruleEvaluator = RuleEvaluator.getInstance();
        Collection<RuleCondition> conditions = new ArrayList<>();
        RuleCondition ruleCondition1 = mock(RuleCondition.class);
        when(ruleCondition1.getConditionId()).thenReturn("cid1");
        when(ruleCondition1.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition1.getOperatorCode()).thenReturn("equals");

        RuleCondition ruleCondition2 = mock(RuleCondition.class);
        when(ruleCondition2.getConditionId()).thenReturn("cid2");
        when(ruleCondition2.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition2.getOperatorCode()).thenReturn("equals");

        RuleCondition ruleCondition3 = mock(RuleCondition.class);
        when(ruleCondition3.getConditionId()).thenReturn("cid3");
        when(ruleCondition3.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition3.getOperatorCode()).thenReturn("equals");


        conditions.add(ruleCondition1);
        conditions.add(ruleCondition2);
        conditions.add(ruleCondition3);
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> objMap = new HashMap<>();

        String expression = "(cid1 OR (cid2 AND cid3))";
        boolean result = ruleEvaluator.evaluateConditionExpression("test-rule",expression, conditions, objMap, resultMap);

        Assertions.assertTrue(result);

        // verify all the methods were called.
        verify(ruleCondition1, times(4)).getConditionId();
        verify(ruleCondition2, times(3)).getConditionId();
        verify(ruleCondition3, times(2)).getConditionId();

    }

    @Test
    public void testEvaluateCelExpression() throws Exception {
        Rule rule = new Rule();
        rule.setRuleId("cel-rule");
        rule.setConditionLanguage("cel");
        rule.setConditionSecurityProfile("standard");
        rule.setExpression("context.user.age >= 18 && 'admin' in context.user.roles && contains_ignore_case(context.user.name, 'hu')");

        Map<String, Object> user = new HashMap<>();
        user.put("age", 21);
        user.put("name", "Steve Hu");
        user.put("roles", java.util.List.of("admin", "user"));
        Map<String,Object> objMap = new HashMap<>();
        objMap.put("user", user);

        boolean result = RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>());

        Assertions.assertTrue(result);
    }

    @Test
    public void testEvaluateStrictCelExpressionWithCuratedRoots() throws Exception {
        Rule rule = new Rule();
        rule.setRuleId("cel-strict-rule");
        rule.setConditionLanguage("cel");
        rule.setConditionSecurityProfile("strict");
        rule.setExpression("auditInfo.subject_claims.ClaimsMap.role == 'admin' && contains_ignore_case(headers.owner, 'hu')");

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("role", "admin");
        Map<String, Object> subjectClaims = new HashMap<>();
        subjectClaims.put("ClaimsMap", claimsMap);
        Map<String, Object> auditInfo = new HashMap<>();
        auditInfo.put("subject_claims", subjectClaims);
        Map<String, Object> headers = new HashMap<>();
        headers.put("owner", "Steve Hu");
        Map<String,Object> objMap = new HashMap<>();
        objMap.put("auditInfo", auditInfo);
        objMap.put("headers", headers);
        objMap.put("internalState", Map.of("tenantSecret", "hidden"));

        boolean result = RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>());

        Assertions.assertTrue(result);
    }

    @Test
    public void testStrictCelExpressionRejectsContextAlias() {
        Rule rule = new Rule();
        rule.setRuleId("cel-strict-context-rule");
        rule.setConditionLanguage("cel");
        rule.setExpression("context.user.age >= 18");

        Map<String,Object> objMap = new HashMap<>();
        objMap.put("user", Map.of("age", 21));

        RuleEngineException exception = Assertions.assertThrows(RuleEngineException.class,
                () -> RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>()));
        Assertions.assertTrue(exception.getMessage().contains("strict profile does not expose full context"));
    }

    @Test
    public void testStrictCelExpressionRejectsRegex() {
        Rule rule = new Rule();
        rule.setRuleId("cel-strict-regex-rule");
        rule.setConditionLanguage("cel");
        rule.setConditionSecurityProfile("strict");
        rule.setExpression("headers.path.matches('^/admin')");

        Map<String,Object> objMap = new HashMap<>();
        objMap.put("headers", Map.of("path", "/admin/users"));

        RuleEngineException exception = Assertions.assertThrows(RuleEngineException.class,
                () -> RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>()));
        Assertions.assertTrue(exception.getMessage().contains("strict profile does not allow regex"));
    }

    @Test
    public void testResponsePhaseCapsStandardCelToStrict() {
        Rule rule = new Rule();
        rule.setRuleId("cel-response-rule");
        rule.setRuleType("res-tra");
        rule.setConditionLanguage("cel");
        rule.setConditionSecurityProfile("standard");
        rule.setExpression("context.responseBody.items.size() > 0");

        Map<String,Object> objMap = new HashMap<>();
        objMap.put("responseBody", Map.of("items", java.util.List.of(1, 2, 3)));

        RuleEngineException exception = Assertions.assertThrows(RuleEngineException.class,
                () -> RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>()));
        Assertions.assertTrue(exception.getMessage().contains("strict profile does not expose full context"));
    }

    @Test
    public void testInternalAdminCelProfileIsDisabledByDefault() {
        Rule rule = new Rule();
        rule.setRuleId("cel-internal-rule");
        rule.setConditionLanguage("cel");
        rule.setConditionSecurityProfile("internal-admin");
        rule.setExpression("context.user.age >= 18");

        Map<String,Object> objMap = new HashMap<>();
        objMap.put("user", Map.of("age", 21));

        RuleEngineException exception = Assertions.assertThrows(RuleEngineException.class,
                () -> RuleEvaluator.getInstance().evaluate(rule, objMap, new HashMap<>()));
        Assertions.assertTrue(exception.getMessage().contains("internal-admin is disabled"));
    }
}
