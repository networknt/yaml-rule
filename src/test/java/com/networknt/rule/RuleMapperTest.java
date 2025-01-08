package com.networknt.rule;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class RuleMapperTest {
    @Test
    public void testString2RuleMap() {
        String ruleString = "rule1:\n" +
                "  ruleId: rule1\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: first rule for unit test\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: equals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "\n" +
                "test-on-date-format-rule:\n" +
                "  ruleId: test-on-date-format-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date on with date format\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: on\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2024-12-16 16:49:00\"\n" +
                "          dateFormat: \"yyyy-MM-dd HH:mm:ss\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-before-date-format-rule:\n" +
                "  ruleId: test-before-date-format-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date before with date format\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: before\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2024-12-16 16:49:00\"\n" +
                "          dateFormat: \"yyyy-MM-dd HH:mm:ss\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-after-date-format-rule:\n" +
                "  ruleId: test-after-date-format-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date after with date format\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: after\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2024-12-16 16:49:00\"\n" +
                "          dateFormat: \"yyyy-MM-dd HH:mm:ss\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-before-rule:\n" +
                "  ruleId: test-before-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date before\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: before\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2099-12-31 23:59:59\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-after-rule:\n" +
                "  ruleId: test-after-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date after\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: after\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2000-01-01 00:00:00\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-on-rule:\n" +
                "  ruleId: test-on-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test date on\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cdate\n" +
                "      operatorCode: on\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2024-12-16 16:49:00\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-len-eq-int-rule:\n" +
                "  ruleId: test-len-eq-int-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string length equal to an integer\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: lengthEquals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 7\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-gte-rule:\n" +
                "  ruleId: test-gte-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test greater than or equal to\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: greaterThanOrEqual\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-lte-rule:\n" +
                "  ruleId: test-lte-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test less than or equal to\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: lessThanOrEqual\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "role-based-auth:\n" +
                "  ruleId: role-based-auth\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Role-based authorization rule for a particuler service with parameters different per endpoint.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-role\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 1\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "role-based-auth-skip-cc:\n" +
                "  ruleId: role-based-auth-skip-cc\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Role-based authorization rule for a particuler service with parameters different per endpoint. Client Credentials token is allowed without role in claims\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: allow-role\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "  conditionExpression: (allow-cc OR allow-role)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "cc-group-role-auth:\n" +
                "  ruleId: cc-group-role-auth\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Role-based authorization rule for account service and allow cc token and transform group to role.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: manager\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: manager\n" +
                "          conditionValue: admin\n" +
                "    - conditionId: teller\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: teller\n" +
                "          conditionValue: frontOffice\n" +
                "    - conditionId: allow-role-jwt\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 4\n" +
                "  conditionExpression: (allow-cc OR manager OR teller OR allow-role-jwt)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "ccsid-group-role-auth:\n" +
                "  ruleId: ccsid-group-role-auth\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Role-based authorization rule for account service and allow cc token with sid claim and transform group to role. This is for the controller and config server.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "    - conditionId: manager\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: manager\n" +
                "          conditionValue: admin\n" +
                "    - conditionId: teller\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: CS\n" +
                "      index: 4\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: teller\n" +
                "          conditionValue: frontOffice\n" +
                "    - conditionId: allow-role-jwt\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 5\n" +
                "  conditionExpression: (allow-cc AND has-sid AND (manager OR teller OR allow-role-jwt))\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "controller-register-access:\n" +
                "  ruleId: controller-register-access\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Allow client credentials token with sid as customized claim. The sid should match the request body serviceId. The register will only accept cc token. It means that only the a running service can register itself. This endpoint shouldn't be called by the portal view at all.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "    - conditionId: has-sid-match-body\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: equals\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: body-serviceId\n" +
                "          expression: true\n" +
                "          conditionValue: requestBody.serviceId\n" +
                "  conditionExpression: (allow-cc AND has-sid AND has-sid-match-body)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "controller-check-access:\n" +
                "  ruleId: controller-check-access\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Allow client credentials token with sid as customized claim. The sid should be contained in the id property of the body in the put request. The check will only accept cc token. It means that only the a running service can check itself to let controler know it is healthy. This endpoint shouldn't be called by the portal view at all.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "    - conditionId: body-id-has-sid\n" +
                "      propertyPath: requestBody.id\n" +
                "      operatorCode: contains\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: sid\n" +
                "          expression: true\n" +
                "          conditionValue: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "  conditionExpression: (allow-cc AND has-sid AND body-id-has-sid)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "controller-deregister-access:\n" +
                "  ruleId: controller-deregister-access\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Allow client credentail token access with sid as customized claim. The sid should match the query parameter serviceId and only cc token is allowed to access. It means that only the a running service can register itself. This endpoint shouldn't be called by the portal view at all.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "    - conditionId: has-sid-match-query\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: equals\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: query-serviceId\n" +
                "          expression: true\n" +
                "          conditionValue: queryParameters.serviceId.First\n" +
                "  conditionExpression: (allow-cc AND has-sid AND has-sid-match-query)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "controller-group-role:\n" +
                "  ruleId: controller-group-role\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Rule that converts a list of AD groups to controller roles before invoking the portal-role-access rule for role-based authentication\n" +
                "  conditions:\n" +
                "    - conditionId: CtlPltRead\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: CtlPltRead\n" +
                "          conditionValue: User_API_Dev_R\n" +
                "    - conditionId: CtlPltWrite\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: CtlPltWrite\n" +
                "          conditionValue: User_API_Dev_W\n" +
                "    - conditionId: CtlPltAdmin\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.groups\n" +
                "      operatorCode: contains\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: CtlPltAdmin\n" +
                "          conditionValue: User_API_Dev_A\n" +
                "  conditionExpression: (CtlPltRead OR CtlPltWrite OR CtlPltAdmin)\n" +
                "  actions:\n" +
                "    - actionId: collect-role\n" +
                "      actionClassName: com.networknt.rule.GroupRoleTransformAction\n" +
                "\n" +
                "config-service-access:\n" +
                "  ruleId: config-service-access\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Rule-based authorization for portal service and allow cc token with sid claim to access the config server configs, certs and files endpoint during the service startup to get config files.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-cc\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.user_id\n" +
                "      operatorCode: isNull\n" +
                "      index: 1\n" +
                "    - conditionId: has-sid\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 2\n" +
                "    - conditionId: sid-match-path\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.sid\n" +
                "      operatorCode: equals\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path-service\n" +
                "          expression: true\n" +
                "          conditionValue: pathParameters.service-name.First\n" +
                "  conditionExpression: (allow-cc AND has-sid AND sid-match-path)\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "\n" +
                "\n" +
                "portal-role-access:\n" +
                "  ruleId: portal-role-access\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: Rule-based authorization for portal service and allow certain roles to access certain endpoints. For AD integration, it transforms the group into a role. This is for the portal services like controller and config server access from the portal view.\n" +
                "  conditions:\n" +
                "    - conditionId: allow-role-jwt\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: isNotNull\n" +
                "      index: 1\n" +
                "  actions:\n" +
                "    - actionId: match-role\n" +
                "      actionClassName: com.networknt.rule.FineGrainedAuthAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: roles\n" +
                "          value: $roles\n" +
                "\n" +
                "get-config-filter:\n" +
                "  ruleId: get-config-filter\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: response-filter\n" +
                "  common: Y\n" +
                "  ruleDesc: For CfgAppRead and CfgAppAdmin roles, the list result will be filtered by the service-name that the user has access in the user profile.\n" +
                "  conditions:\n" +
                "    - conditionId: row-filter-role\n" +
                "      propertyPath: auditInfo.subject_claims.ClaimsMap.roles\n" +
                "      operatorCode: inList\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: role-list\n" +
                "          conditionValue: CfgAppRead CfgAppAdmin\n" +
                "  actions:\n" +
                "    - actionId: filter-row\n" +
                "      actionClassName: com.networknt.rule.RoleFilterRowAction\n" +
                "      conditionResult: true\n" +
                "    - actionId: filter-col\n" +
                "      actionClassName: com.networknt.rule.RoleFilterColAction\n" +
                "      conditionResult: true\n" +
                "\n" +
                "petstore-group-role-transform:\n" +
                "  ruleId: petstore-group-role-transform\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: request-access\n" +
                "  common: Y\n" +
                "  ruleDesc: A rule to transform a list of Active Directory groups to a list of roles for petstore API\n" +
                "  conditions:\n" +
                "    - conditionId: manager\n" +
                "      propertyPath: groups\n" +
                "      operatorCode: contains\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: manager\n" +
                "          conditionValue: admin\n" +
                "    - conditionId: teller\n" +
                "      propertyPath: groups\n" +
                "      operatorCode: contains\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: teller\n" +
                "          conditionValue: frontOffice\n" +
                "  conditionExpression: (manager OR teller)\n" +
                "\n" +
                "petstore-response-header-replace:\n" +
                "  ruleId: petstore-response-header-replace\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: response-transform\n" +
                "  common: Y\n" +
                "  ruleDesc: Transform the response to replace one header with the other header.\n" +
                "  conditions:\n" +
                "    - conditionId: path-pets\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: equals\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v1/pets\n" +
                "    - conditionId: path-dogs\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: equals\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v1/dogs\n" +
                "    - conditionId: path-cats\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: equals\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v1/cats\n" +
                "  conditionExpression: (path-pets OR path-dogs OR path-cats)\n" +
                "  actions:\n" +
                "    - actionId: header-transform\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: sourceHeader\n" +
                "          value: X-Test-1\n" +
                "        - actionValueId: targetHeader\n" +
                "          value: My-Header\n" +
                "\n" +
                "petstore-response-header-replace-in:\n" +
                "  ruleId: petstore-response-header-replace-in\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: response-transform\n" +
                "  common: Y\n" +
                "  ruleDesc: Transform the response to replace one header with the other header.\n" +
                "  conditions:\n" +
                "    - conditionId: path-pets\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: inList\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: pets\n" +
                "          conditionValue: /v1/pets\n" +
                "        - conditionValueId: dogs\n" +
                "          conditionValue: /v1/dogs\n" +
                "        - conditionValueId: cats\n" +
                "          conditionValue: /v1/cats\n" +
                "  actions:\n" +
                "    - actionId: header-transform\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: sourceHeader\n" +
                "          value: X-Test-1\n" +
                "        - actionValueId: targetHeader\n" +
                "          value: My-Header\n" +
                "\n" +
                "petstore-response-header-replace-match:\n" +
                "  ruleId: petstore-response-header-replace-match\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: response-transform\n" +
                "  common: Y\n" +
                "  ruleDesc: Transform the response to replace one header with the other header.\n" +
                "  conditions:\n" +
                "    - conditionId: BankingServices\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: match\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v3/(.*)/BankingServices/(.*)\n" +
                "    - conditionId: insight\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: match\n" +
                "      index: 2\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v3/(.*)/insight/login/(.*)/attributes/(.*)\n" +
                "    - conditionId: Investments\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: match\n" +
                "      index: 3\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v3/(.*)/Investments\n" +
                "    - conditionId: attributes\n" +
                "      propertyPath: requestPath\n" +
                "      operatorCode: match\n" +
                "      index: 4\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: path\n" +
                "          conditionValue: /v3/(.*)/attributes/(.*)\n" +
                "  conditionExpression: (BankingServices OR insight OR Investments OR attributes)\n" +
                "\n" +
                "test-priority-rule:\n" +
                "  ruleId: test-priority-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule priority\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: greaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 5\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-priority-rule2:\n" +
                "  ruleId: test-priority-rule2\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule priority\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: greaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-len-eq-rule:\n" +
                "  ruleId: test-len-eq-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string length equal\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: lengthEquals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-len-gt-rule:\n" +
                "  ruleId: test-len-gt-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string \"ClassAA\" length greater than another string length \"6\" and the result is true\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: lengthGreaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-len-gt-rule-int:\n" +
                "  ruleId: test-len-gt-rule-int\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string \"ClassAA\" length greater than an integer 9 and the result is false\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: lengthGreaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 9\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-len-lt-rule:\n" +
                "  ruleId: test-len-lt-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string length less than\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: lengthLessThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-match-rule:\n" +
                "  ruleId: test-match-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string match\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: match\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: Class.*\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "      conditionResult: true\n" +
                "\n" +
                "test-not-match-rule:\n" +
                "  ruleId: test-not-match-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string not match\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: notMatch\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: ClassB.*\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-match-rule-flags:\n" +
                "  ruleId: test-match-rule-flags\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string match with flags\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: match\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: CLASSa\n" +
                "          regexFlags: i\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-empty-rule:\n" +
                "  ruleId: test-empty-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string empty\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cname\n" +
                "      operatorCode: isEmpty\n" +
                "      index: 0\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-not-empty-rule:\n" +
                "  ruleId: test-not-empty-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string not empty\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cname\n" +
                "      operatorCode: isNotEmpty\n" +
                "      index: 0\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-blank-rule:\n" +
                "  ruleId: test-blank-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string blank\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cname\n" +
                "      operatorCode: isBlank\n" +
                "      index: 0\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-not-blank-rule:\n" +
                "  ruleId: test-not-blank-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test string not blank\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cname\n" +
                "      operatorCode: isNotBlank\n" +
                "      index: 0\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-single-action-true:\n" +
                "  ruleId: test-single-action-true\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: If a rule has only one action, it is executed only if the evaluation result is true\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      conditionDesc: The name is ClassA and its length is greater than 4, so the condition is true\n" +
                "      propertyPath: \"name\"\n" +
                "      operatorCode: lengthGreaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 4\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: actionIfTrue\n" +
                "      actionDesc: This action is executed only if the rule evaluation is true\n" +
                "      actionClassName: com.networknt.rule.MultipleActionOne\n" +
                "\n" +
                "test-single-action-false:\n" +
                "  ruleId: test-single-action-false\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: If a rule has only one action, it won't be executed if the evaluation result is false\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      conditionDesc: The name is ClassA and its length is less than 10, so the condition is false\n" +
                "      propertyPath: \"name\"\n" +
                "      operatorCode: lengthGreaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 10\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: actionIfTrue\n" +
                "      actionDesc: This action will not be executed as the evaluation result is false\n" +
                "      actionClassName: com.networknt.rule.MultipleActionOne\n" +
                "\n" +
                "test-multiple-actions:\n" +
                "  ruleId: test-multiple-actions\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test multiple actions, MultipleActionOne is executed if the condition is true, MultipleActionTwo is executed if the condition is false. MultipleActionThree is executed regardless of the evaluation result.\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: isNotEmpty\n" +
                "      index: 0\n" +
                "  actions:\n" +
                "    - actionId: actionOne\n" +
                "      actionDesc: This action is executed as the condition is true\n" +
                "      actionClassName: com.networknt.rule.MultipleActionOne\n" +
                "      conditionResult: true\n" +
                "    - actionId: actionTwo\n" +
                "      actionDesc: This action is not executed as the condition is false\n" +
                "      actionClassName: com.networknt.rule.MultipleActionTwo\n" +
                "      conditionResult: false\n" +
                "    - actionId: actionThree\n" +
                "      actionDesc: This action is executed regardless evaluation result is true or false\n" +
                "      actionClassName: com.networknt.rule.MultipleActionThree\n" +
                "\n" +
                "condition_id_reference:\n" +
                "  ruleId: condition_id_reference\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: first rule for unit test\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: equals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 6\n" +
                "          valueTypeCode: STRING\n" +
                "    - conditionId: cid2\n" +
                "      propertyPath: ClassA.Bobject.Cobject.Cint\n" +
                "      operatorCode: greaterThan\n" +
                "      index: 1\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv2\n" +
                "          conditionValue: 7\n" +
                "          valueTypeCode: STRING\n" +
                "    - conditionId: cid3\n" +
                "      propertyPath: ClassA.Aname\n" +
                "      operatorCode: isNotEmpty\n" +
                "      index: 2\n" +
                "  conditionExpression: \"(cid1 OR (cid2 AND cid3))\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "\n" +
                "test-contains-ignore-case-rule:\n" +
                "  ruleId: test-contains-ignore-case-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with containsIgnoreCase custom operator\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: containsIgnoreCase\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"test\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-starts-with-rule:\n" +
                "  ruleId: test-starts-with-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with startsWith custom operator\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: startsWith\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"Test\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-built-in-operators-rule:\n" +
                "  ruleId: test-built-in-operators-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with built-in operators and expressions\n" +
                "  conditionExpression: \"cid1 AND (cid2 OR cid3)\"\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: age\n" +
                "      operatorCode: greaterThan\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 30\n" +
                "    - conditionId: cid2\n" +
                "      propertyPath: city\n" +
                "      operatorCode: equals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv2\n" +
                "          conditionValue: \"New York\"\n" +
                "    - conditionId: cid3\n" +
                "      propertyPath: country\n" +
                "      operatorCode: contains\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv3\n" +
                "          conditionValue: \"US\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-regex-rule:\n" +
                "  ruleId: test-regex-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with regex\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: match\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"abc.*\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-date-rule:\n" +
                "  ruleId: test-date-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with date operator\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: eventDate\n" +
                "      operatorCode: on\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: \"2024-05-03 10:00:00\"\n" +
                "          dateFormat: \"yyyy-MM-dd HH:mm:ss\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "test-length-rule:\n" +
                "  ruleId: test-length-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with length operator\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: lengthEquals\n" +
                "      index: 0\n" +
                "      conditionValues:\n" +
                "        - conditionValueId: cv1\n" +
                "          conditionValue: 4\n" +
                "          valueTypeCode: INTEGER\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.ValidationAction\n" +
                "\n" +
                "\n" +
                "test-set-header-rule:\n" +
                "  ruleId: test-set-header-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule that sets a header with value from objMap\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: equals\n" +
                "      conditionValues:\n" +
                "        - conditionValue: \"test\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.TestSetHeaderAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: X-Test-Name\n" +
                "          value: ${name}\n" +
                "\n" +
                "test-set-header-with-result-rule:\n" +
                "  ruleId: test-set-header-with-result-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule that sets a header with result from conditions\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: equals\n" +
                "      conditionValues:\n" +
                "        - conditionValue: \"test\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.TestSetHeaderAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: X-Test-Result\n" +
                "          value: ^{cid1}\n" +
                "\n" +
                "test-set-header-nested-rule:\n" +
                "  ruleId: test-set-header-nested-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule that sets a header with value from a nested property\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: address.city\n" +
                "      operatorCode: equals\n" +
                "      conditionValues:\n" +
                "        - conditionValue: \"New York\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.TestSetHeaderAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: X-Test-City\n" +
                "          value: ${address.city}\n" +
                "test-set-header-no-match-rule:\n" +
                "  ruleId: test-set-header-no-match-rule\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test rule with no match and no header is set\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: name\n" +
                "      operatorCode: equals\n" +
                "      conditionValues:\n" +
                "        - conditionValue: \"abc\"\n" +
                "  actions:\n" +
                "    - actionId: act1\n" +
                "      actionClassName: com.networknt.rule.TestSetHeaderAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: X-Test-Name\n" +
                "          value: ${name}\n" +
                "\n" +
                "\n" +
                "test-set-header-optional-code:\n" +
                "  ruleId: test-set-header-optional-code\n" +
                "  hostId: N2CMw0HGQXeLvC1wBfln2A\n" +
                "  ruleType: generic\n" +
                "  common: Y\n" +
                "  ruleDesc: test set header action without valueTypeCode\n" +
                "  conditions:\n" +
                "    - conditionId: cid1\n" +
                "      propertyPath: age\n" +
                "      operatorCode: equals\n" +
                "      conditionValues:\n" +
                "        - conditionValue: 30\n" +
                "  actions:\n" +
                "    - actionId: setHeader1\n" +
                "      actionClassName: com.networknt.rule.TestSetHeaderAction\n" +
                "      actionValues:\n" +
                "        - actionValueId: X-Test-Age\n" +
                "          value: ${age}\n";
        Map<String, Rule> rules = RuleMapper.string2RuleMap(ruleString);
        System.out.println("rules size = " + rules.size());
    }
}
