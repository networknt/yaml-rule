package com.networknt.rule;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class RuleMapperTest {
    @Test
    public void testString2RuleMap() {
        String ruleString = "\n" +
                "controller-register-access:\n" +
                "  ruleId: controller-register-access\n" +
                "  host: lightapi.net\n" +
                "  ruleType: request-access\n" +
                "  visibility: public\n" +
                "  description: Allow client credentials token with sid as customized claim. The sid should match the request body serviceId. The register will only accept cc token. It means that only the a running service can register itself. This endpoint shouldn't be called by the portal view at all.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: NIL\n" +
                "      joinCode: AND\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: NNIL\n" +
                "      joinCode: AND\n" +
                "      index: 2\n" +
                "    - conditionId: has-sid-match-body\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: EQ\n" +
                "      joinCode: AND\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: body-serviceId\n" +
                "          expression: true\n" +
                "          conditionValue: requestBody.serviceId\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "controller-deregister-access:\n" +
                "  ruleId: controller-deregister-access\n" +
                "  host: lightapi.net\n" +
                "  ruleType: request-access\n" +
                "  visibility: public\n" +
                "  description: Allow client credentail token access with sid as customized claim. The sid should match the query parameter serviceId and only cc token is allowed to access. It means that only the a running service can register itself. This endpoint shouldn't be called by the portal view at all.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: NIL\n" +
                "      joinCode: AND\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: NNIL\n" +
                "      joinCode: AND\n" +
                "      index: 2\n" +
                "    - conditionId: has-sid-match-query\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: EQ\n" +
                "      joinCode: AND\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: query-serviceId\n" +
                "          expression: true\n" +
                "          conditionValue: queryParameters.serviceId.First\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n";
        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        System.out.println("rules size = " + rules.size());
    }
}
