package com.networknt.rule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CompactRuleFormatTest {
    @Test
    public void testCompactRuleFormatAliases() throws Exception {
        String ruleString = "allow-attribute-based-access-control.lightapi.net:\n" +
                "  ruleId: allow-attribute-based-access-control.lightapi.net\n" +
                "  hostId: 01964b05-552a-7c4b-9184-6857e7f3dc5f\n" +
                "  ruleName: Attribute-Based Access Control for authorization code token.\n" +
                "  version: 1.0.0\n" +
                "  author: 01964b05-5532-7c79-8cde-191dcbd421b8\n" +
                "  ruleType: req-acc\n" +
                "  common: Y\n" +
                "  ruleDesc: If the token has att claim, then at least one att in the JWT att claim matches the endpoint permission attributes configuration.\n" +
                "  conditions:\n" +
                "    - conditionId: attribute-is-not-null\n" +
                "      conditionDesc: att in the claim map is not null.\n" +
                "      operator: exists\n" +
                "      operand: auditInfo.subject_claims.ClaimsMap.att\n" +
                "  actions:\n" +
                "    - actionId: validate\n" +
                "      actionRef: validation\n" +
                "      actionValues:\n" +
                "        message: Access granted\n";

        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        Rule rule = rules.get("allow-attribute-based-access-control.lightapi.net");
        Assertions.assertEquals("01964b05-552a-7c4b-9184-6857e7f3dc5f", rule.getHostId());
        Assertions.assertEquals("1.0.0", rule.getRuleVersion());
        Assertions.assertEquals("01964b05-5532-7c79-8cde-191dcbd421b8", rule.getRuleOwner());

        RuleCondition condition = rule.getConditions().iterator().next();
        Assertions.assertEquals("auditInfo.subject_claims.ClaimsMap.att", condition.getPropertyPath());
        Assertions.assertEquals("isNotNull", condition.getOperatorCode());
        Assertions.assertEquals("att in the claim map is not null.", condition.getConditionDesc());
        RuleAction action = rule.getActions().iterator().next();
        Assertions.assertEquals("validation", action.getActionRef());
        Assertions.assertEquals("message", action.getActionValues().iterator().next().getActionValueId());

        RuleEngine engine = new RuleEngine(rules, null);
        engine.registerAction("validation", new ValidationAction());
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("att", "department:it");
        Map<String, Object> subjectClaims = new HashMap<>();
        subjectClaims.put("ClaimsMap", claimsMap);
        Map<String, Object> auditInfo = new HashMap<>();
        auditInfo.put("subject_claims", subjectClaims);
        Map<String, Object> input = new HashMap<>();
        input.put("auditInfo", auditInfo);

        Map<String, Object> result = engine.executeRule("allow-attribute-based-access-control.lightapi.net", input);
        Assertions.assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT));
    }

    @Test
    public void testCompactRuleFormatListAndLengthOperators() throws Exception {
        String ruleString = "name-policy.lightapi.net:\n" +
                "  ruleId: name-policy.lightapi.net\n" +
                "  ruleName: Name policy\n" +
                "  ruleType: req-acc\n" +
                "  common: Y\n" +
                "  conditions:\n" +
                "    - conditionId: status-is-allowed\n" +
                "      operator: inList\n" +
                "      operand: user.status\n" +
                "      expected: [active, pending]\n" +
                "    - conditionId: name-is-long-enough\n" +
                "      operator: lengthGreaterThan\n" +
                "      operand: user.name\n" +
                "      expected: 10\n";

        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        RuleEngine engine = new RuleEngine(rules, null);

        Map<String, Object> user = new HashMap<>();
        user.put("status", "active");
        user.put("name", "Steve Hu Test");
        Map<String, Object> input = new HashMap<>();
        input.put("user", user);

        Map<String, Object> result = engine.executeRule("name-policy.lightapi.net", input);
        Assertions.assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT));
    }

    @Test
    public void testCompactRuleFormatParsesJsonArrayStringExpected() throws Exception {
        String ruleString = "status-policy.lightapi.net:\n" +
                "  ruleId: status-policy.lightapi.net\n" +
                "  ruleName: Status policy\n" +
                "  ruleType: req-acc\n" +
                "  common: Y\n" +
                "  conditions:\n" +
                "    - conditionId: status-is-allowed\n" +
                "      operator: inList\n" +
                "      operand: user.status\n" +
                "      expected: '[\"active\",\"pending\"]'\n";

        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        RuleEngine engine = new RuleEngine(rules, null);

        Map<String, Object> user = new HashMap<>();
        user.put("status", "active");
        Map<String, Object> input = new HashMap<>();
        input.put("user", user);

        Map<String, Object> result = engine.executeRule("status-policy.lightapi.net", input);
        Assertions.assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT));
    }

    @Test
    public void testCompactRuleFormatCollectionOperators() throws Exception {
        String ruleString = "scope-policy.lightapi.net:\n" +
                "  ruleId: scope-policy.lightapi.net\n" +
                "  ruleName: Scope policy\n" +
                "  ruleType: req-acc\n" +
                "  common: Y\n" +
                "  conditions:\n" +
                "    - conditionId: has-readable-scope\n" +
                "      operator: containsAny\n" +
                "      operand: token.scopes\n" +
                "      expected: [portal.r, portal.w]\n" +
                "    - conditionId: has-baseline-scopes\n" +
                "      operator: containsAll\n" +
                "      operand: token.scopes\n" +
                "      expected: [portal.r, profile.r]\n" +
                "    - conditionId: has-no-dangerous-scopes\n" +
                "      operator: containsNone\n" +
                "      operand: token.scopes\n" +
                "      expected: [admin.delete]\n";

        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        RuleEngine engine = new RuleEngine(rules, null);

        Map<String, Object> token = new HashMap<>();
        token.put("scopes", java.util.List.of("portal.r", "profile.r"));
        Map<String, Object> input = new HashMap<>();
        input.put("token", token);

        Map<String, Object> result = engine.executeRule("scope-policy.lightapi.net", input);
        Assertions.assertEquals(Boolean.TRUE, result.get(RuleConstants.RESULT));
    }
}
