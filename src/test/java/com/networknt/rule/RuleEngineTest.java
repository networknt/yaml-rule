package com.networknt.rule;

import com.networknt.rule.custom.CustomOperator;
import com.networknt.rule.custom.StartsWithOperator;
import com.networknt.rule.exception.RuleEngineException;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineTest {
    static Logger logger = LoggerFactory.getLogger(RuleEngineTest.class);

    static Map<String, Rule> ruleMap;
    static Map<String, Collection<Rule>> groupMap;

    @BeforeAll
    public static void init(){
        System.out.println("BeforeAll init() method called to load rules");
        InputStream stream = RuleEngineTest.class.getResourceAsStream("/rules.yml");
        String text = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        System.out.println(text);
        ruleMap = RuleMapper.string2RuleMap(text);
        System.out.println(ruleMap);
    }

    @Test
    public void testRuleExecute1() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        engine.executeRule("rule1", objMap);
    }

    @Test
    public void testRuleExecute2() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(1);
        objectC.setCdate(new java.util.Date());
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);

        Assertions.assertThrows(RuleEngineException.class, () -> {
            engine.executeRule("rule2", objMap);
        });    }

    @Test
    public void testRoleAuthWithCcToken() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1MjkwMDY1MCwianRpIjoiM2VVdzdLUXpjTnl6c3d6OTlfRFZCdyIsImlhdCI6MTYzNzU0MDY1MCwibmJmIjoxNjM3NTQwNTMwLCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X2lkIjoiZjdkNDIzNDgtYzY0Ny00ZWZiLWE1MmQtNGM1Nzg3NDIxZTczIiwic2NvcGUiOiJhY2NvdW50LnIgYWNjb3VudC53In0.M68F5O2ZlGpwJbxa91kOjRfNcbe0-_s6FEubPP1fjAp2MItZyyzkvnqMLrKlLv9ZbCiYiXKuBH1NDTOt93sDBzqlz7FeFutnxpUfNZhbg_dwhnVlWTvWmrQuFCILRDgKFlXRkLKcihZJI9OpjWMhno4WD5OmN6coyNRcoewhwS8Sg3UsGRobjSlKbc1Fo14_l6RaUdvX7AsPCC5J2uzajOG5a9oQiRVPJ1W4ecVPyYqdqBsWoUVZcsBLZcvnAagqzMBvoDQKmhlJ7WhmOw2fZxOeZSrrRtYBfdlC0xgdc6Lgi3R-W3ZdNAxhJ-Xypb06OpTR05FUuAJ639BIUo8_mQ";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        Map<String, Object> auditInfo = new HashMap();
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        JwtClaims claims = jwtContext.getJwtClaims();
        auditInfo.put("subject_claims", claims);
        objMap.put("auditInfo", auditInfo);
        objMap.put("roles", "manager,teller");
        Map<String, Object> result = engine.executeRule("cc-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthCustomerRightRole() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1MjkwMTMxOCwianRpIjoiZnhDNGYzRjVxbWg5ZWczem80TEZWQSIsImlhdCI6MTYzNzU0MTMxOCwibmJmIjoxNjM3NTQxMTk4LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHUiLCJ1c2VyX3R5cGUiOiJDVVNUT01FUiIsImNsaWVudF9pZCI6ImY3ZDQyMzQ4LWM2NDctNGVmYi1hNTJkLTRjNTc4NzQyMWU3MiIsInJvbGVzIjoiY3VzdG9tZXIiLCJzY29wZSI6WyJhY2NvdW50LnIiLCJhY2NvdW50LnciXX0.bfG2okhBhgif2Jty60mGJKz2TKCtW219c2kcBVKznWctVmns8g0r0sztR_N2EBWZ-UUpA0Bm9kTo5DHoSGHM28t-46RSH_RdaTNGsRg74zLC_HJWuc6hGQl05jU-vltNNFPQ3CA0__yRNEi1zLqICtbqvmlcl0uHd_PnPeFvjNDRY68Qyr7PN_YXYbVT7dRiauqrWsslLZKbY0-Bpk8Ro6pJ03akX0-3pdd1Jy9HryyEPFw4OEwCqU2G_OETcZ2qNf-fKZwYLC9kofku9CehWkYhujpnuaFSOuGEGfB-eqi4tTHKA2YmaE-GsYUyFNa8H4cHTAGlKUmDRKRdV-em5Q";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        Map<String, Object> auditInfo = new HashMap();
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        JwtClaims claims = jwtContext.getJwtClaims();
        auditInfo.put("subject_claims", claims);
        objMap.put("auditInfo", auditInfo);
        objMap.put("roles", "manager teller customer");
        Map<String, Object> result = engine.executeRule("cc-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    @Disabled
    public void testRoleAuthWrongRole() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1MjkwMTQyMiwianRpIjoiNk01a242S3kwYUU3M2tIUkdJZnBjdyIsImlhdCI6MTYzNzU0MTQyMiwibmJmIjoxNjM3NTQxMzAyLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHUiLCJ1c2VyX3R5cGUiOiJDVVNUT01FUiIsImNsaWVudF9pZCI6ImY3ZDQyMzQ4LWM2NDctNGVmYi1hNTJkLTRjNTc4NzQyMWU3MiIsInJvbGVzIjoidXNlciIsInNjb3BlIjpbImFjY291bnQuciIsImFjY291bnQudyJdfQ.VqExehutVEmo7qCuffCJZAPBWvoqihTWBuTBiXbR9x9vQbzPiFI0qr0FDilTxdqSOtEmW3ml9eioR2tLswLVp0NQnuHw5ElPYNfTsvIW8xLm3hcTAMk08Xhpg7TJn6_Z3zDNwv3mDn-ZMzB9R80O-OI61W2XAuWzCdOIEffcMTZa6VMB3e0tKLN3SnmKL5LJmbAxfuy8CK1QwfRLvhZgNYggd1XAyKCEB33VDEV0rKUJlwSRKXYZbKcvT1r1MojtP8JlReW9h_Xfx3CRH4VxzcAuVQyVrLd7bOpB03eVSkOTw9I4dgCe6ODELERrvGlsQ9aiETIn7rCrRsN9dt5mAw";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        Map<String, Object> auditInfo = new HashMap();
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        JwtClaims claims = jwtContext.getJwtClaims();
        auditInfo.put("subject_claims", claims);
        objMap.put("auditInfo", auditInfo);
        objMap.put("roles", "manager teller customer");
        Map<String, Object> result = engine.executeRule("cc-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthWithRightGroup() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1MjkwMTU0NiwianRpIjoiUFVBQW5rVWdld3dsSkpDMkxCMUNvdyIsImlhdCI6MTYzNzU0MTU0NiwibmJmIjoxNjM3NTQxNDI2LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHUiLCJ1c2VyX3R5cGUiOiJFTVBMT1lFRSIsImNsaWVudF9pZCI6ImY3ZDQyMzQ4LWM2NDctNGVmYi1hNTJkLTRjNTc4NzQyMWU3MiIsImdyb3VwcyI6ImFkbWluIGZyb250T2ZmaWNlIiwic2NvcGUiOlsiYWNjb3VudC5yIiwiYWNjb3VudC53Il19.nW6tu-L1qwnNMWdoEX-iAE04nlYB4rNXFYHtVS6aTBV1cwnRQZj7UygwrroOBRaRrsJKXMXkpXJ9MDfjGSurbMKZIZ-4iwqj2MK1xNnjSMIHj1hM0llNKtvjFCTlc-XJYFmvNbp6SW5YK47I3FVSRLNFEKopx75NpQu-hG_ysNbAcAoFXS8JA7bdb9SHxlbhbELBbbT7RB7GvifrU4_rvYD6PDAtRcHUOZtNBM1QbHSMSUa26P7mc7GinIC_zLJYHVblieNWvBzGdkhjhe5CQaE5mrjJvjJZUozfjg85hhRK4p_JkHz9urD9RDNnGF0u9LL1wR1QYK8USQiui-TVOw";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        Map<String, Object> auditInfo = new HashMap();
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        JwtClaims claims = jwtContext.getJwtClaims();
        auditInfo.put("subject_claims", claims);
        objMap.put("auditInfo", auditInfo);
        objMap.put("roles", "manager teller customer");
        Map<String, Object> result = engine.executeRule("cc-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthWithWrongGroup() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1MjkwMjY3MCwianRpIjoicnFXa29IVXhhLVVuTDZXRGJQbko1QSIsImlhdCI6MTYzNzU0MjY3MCwibmJmIjoxNjM3NTQyNTUwLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHUiLCJ1c2VyX3R5cGUiOiJFTVBMT1lFRSIsImNsaWVudF9pZCI6ImY3ZDQyMzQ4LWM2NDctNGVmYi1hNTJkLTRjNTc4NzQyMWU3MiIsImdyb3VwcyI6ImJhY2tPZmZpY2UiLCJzY29wZSI6WyJhY2NvdW50LnIiLCJhY2NvdW50LnciXX0.UOi8a6bAzOnbIsFYlOZ9wvkKZbSZ8CHZg3VgGNZ_e287K-lWROMRIzfJOvud0IC6dWH8svIhME-c7lo6bL-4qd2juEMzIzbUSPYp7CX8iSpa1HEu6gYmdP6iSENQz9DwG9wxUwRwHZZOEaNubppdPGPUSIDW-Xz1PwslfgIyUHnVwhPjEpwlVPlQmEKr4in5N7EOUmpe8_MIo6brPBERhqtdljQr0luB9hafY0-ErYWqZDpZmbr8VxEx2kx4AItBkKi4GtYUiIUOum3SrAFZKz8CbBEKWtT_h6GPcI6NHWJGNOnpBbFyy0rG66_-EDo3-Br7VjNJqzt2Gg3dbNO50A";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        Map<String, Object> auditInfo = new HashMap();
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        JwtContext jwtContext = consumer.process(jwt);
        JwtClaims claims = jwtContext.getJwtClaims();
        auditInfo.put("subject_claims", claims);
        objMap.put("auditInfo", auditInfo);
        objMap.put("roles", "manager teller customer");
        Map<String, Object> result = engine.executeRule("cc-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testGroup2RoleTransformBoth() throws Exception {
        String groups = "admin frontOffice";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("groups", groups);
        Map<String, Object> result = engine.executeRule("petstore-group-role-transform", objMap);
        assertTrue((Boolean)result.get("manager"));
        assertTrue((Boolean)result.get("teller"));
    }

    @Test
    public void testGroup2RoleTransformManager() throws Exception {
        String groups = "admin";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("groups", groups);
        Map<String, Object> result = engine.executeRule("petstore-group-role-transform", objMap);
        assertTrue((Boolean)result.get("manager"));
        Assertions.assertFalse((Boolean)result.get("teller"));
    }

    @Test
    public void testResponseHeaderReplaceRule() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("requestPath", "/v1/dogs");
        Map<String, Object> result = engine.executeRule("petstore-response-header-replace", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testResponseHeaderReplaceRuleInFalse() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("requestPath", "/v1/eggs");
        Map<String, Object> result = engine.executeRule("petstore-response-header-replace-in", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testResponseHeaderReplaceRuleInTrue() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("requestPath", "/v1/dogs");
        Map<String, Object> result = engine.executeRule("petstore-response-header-replace-in", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testResponseHeaderReplaceRuleMatch() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        // objMap.put("requestPath", "/v3/customerId/BankingServices/Token");
        // objMap.put("requestPath", "/v3/customerId/Investments");
        // objMap.put("requestPath", "/v3/customerId/insight/login/loginId/attributes/requestId/GetLendingAttributes");
        objMap.put("requestPath", "/v3/customerId/attributes/library");
        Map<String, Object> result = engine.executeRule("petstore-response-header-replace-match", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRulePriority() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-priority-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
        objectC.setCint(7);
        result = engine.executeRule("test-priority-rule2", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLenEq() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-len-eq-rule", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleEmpty() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("");
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-empty-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleNotEmpty() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("  ");
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-not-empty-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleBlank() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("  ");
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-blank-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleNotBlank() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-not-blank-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLenEqInteger() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassAA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-len-eq-int-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleGte() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassAA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-gte-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLte() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("Class");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-lte-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLenGt() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassAA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-len-gt-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLenGtIntType() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassAA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-len-gt-rule-int", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleLenLt() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("Class");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-len-lt-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleMatch() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-match-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleNotMatch() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("ClassA", objectA);
        Map result = engine.executeRule("test-not-match-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleBefore() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        objectC.setCdate(new java.util.Date(System.currentTimeMillis() + 10000));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-before-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleAfter() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        objectC.setCdate(new java.util.Date(System.currentTimeMillis() - 10000));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-after-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleOn() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        objectC.setCdate(new java.util.Date());
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-on-rule", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleOnTrue() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectC.setCdate(df.parse("2024-12-16 16:49:00"));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-on-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleOnDateFormatWithObject() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectC.setCdate(df.parse("2024-12-16 16:49:00"));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-on-date-format-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleBeforeDateFormat() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectC.setCdate(df.parse("2024-12-15 16:49:00"));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-before-date-format-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRuleAfterDateFormat() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassC objectC = new ClassC();
        objectC.setCname("ClassC");
        objectC.setCint(6);
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectC.setCdate(df.parse("2024-12-17 16:49:00"));
        ClassB objectB = new ClassB();
        objectB.setBname("ClassB");
        objectB.setCobject(objectC);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        objectA.setBobject(objectB);
        Map objMap = new HashMap();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-after-date-format-rule", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testMultipleActionsTrue() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassA objectA = new ClassA();
        objectA.setAname("ClassA");
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-multiple-actions", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get("MultipleActionOne"));
        assertTrue((Boolean)result.get("MultipleActionThree"));
        Assertions.assertFalse(result.containsKey("MultipleActionTwo"));
    }

    @Test
    public void testMultipleActionsFalse() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        ClassA objectA = new ClassA();
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("ClassA", objectA);
        Map<String, Object> result = engine.executeRule("test-multiple-actions", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get("MultipleActionTwo"));
        assertTrue((Boolean)result.get("MultipleActionThree"));
        Assertions.assertFalse(result.containsKey("MultipleActionOne"));
    }

    /**
     * Test single action with true result and the action is executed
     * @throws Exception exception
     */
    @Test
    public void testSingleActionTrue() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "ClassA");
        Map<String, Object> result = engine.executeRule("test-single-action-true", objMap);
        assertTrue((Boolean)result.get(RuleConstants.RESULT));
        assertTrue((Boolean)result.get("MultipleActionOne"));
    }

    /**
     * Test single action with false result and the action is not executed
     * @throws Exception exception
     */
    @Test
    public void testSingleActionFalse() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "ClassA");
        Map<String, Object> result = engine.executeRule("test-single-action-false", objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
        Assertions.assertFalse(result.containsKey("MultipleActionOne"));
    }

    @Test
    void testCustomOperatorContainsIgnoreCase() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-contains-ignore-case-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "Test String");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "not match");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", null);
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "test");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "tEsT");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }


    @Test
    void testCustomOperatorStartsWith() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-starts-with-rule";

        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "Test String");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "not match");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", null);
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "Test");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "test");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

    }
    @Test
    void testRuleWithBuiltInOperators() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-built-in-operators-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("age", 35);
        objMap.put("city", "New York");
        objMap.put("country", "USA");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("age", 25);
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    void testCustomOperatorRegistration() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-starts-with-rule";

        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "Test String");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        CustomOperator customOperator = engine.getCustomOperator("startsWith");
        Assertions.assertNotNull(customOperator);
        assertEquals("startsWith", customOperator.getOperatorName());

        StartsWithOperator startsWithOperator = new StartsWithOperator();
        engine.registerCustomOperator("startsWith1", startsWithOperator );
        CustomOperator customOperator1 = engine.getCustomOperator("startsWith1");
        Assertions.assertNotNull(customOperator1);
        assertEquals("startsWith", customOperator1.getOperatorName());

    }

    @Test
    void testRuleWithRegexOperator() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-regex-rule";

        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "abc String");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", "def String");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

    }
    @Test
    void testRuleOnDateFormat() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-date-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("eventDate", java.sql.Timestamp.valueOf("2024-05-03 10:00:00"));
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
        objMap.put("eventDate", java.sql.Timestamp.valueOf("2024-05-04 10:00:00"));
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
        objMap.put("eventDate", "2024-05-03 10:00:00");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));


    }

    @Test
    void testRuleWithLengthOperator() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-length-rule";

        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "Test");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
        objMap.put("name", "Test1");
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));

        objMap.put("name", null);
        result = engine.executeRule(ruleId, objMap);
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    void testSetHeaderAction() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-set-header-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "test");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        assertEquals("test", result.get("X-Test-Name"));

        objMap.put("name", "abc");
        result = engine.executeRule(ruleId, objMap);
        assertNull(result.get("X-Test-Name"));

    }

    @Test
    void testSetHeaderWithResultAction() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-set-header-with-result-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "test");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        assertEquals(true, result.get("X-Test-Result"));


        objMap.put("name", "abc");
        result = engine.executeRule(ruleId, objMap);
        assertNull(result.get("X-Test-Result"));
    }

    @Test
    void testSetHeaderNestedAction() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-set-header-nested-rule";
        Map<String, Object> objMap = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");
        objMap.put("address", address);
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        assertEquals("New York", result.get("X-Test-City"));

        address.put("city", "London");
        result = engine.executeRule(ruleId, objMap);
        assertNull(result.get("X-Test-City"));

    }

    @Test
    void testSetHeaderNoMatchAction() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-set-header-no-match-rule";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("name", "test");
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        assertNull(result.get("X-Test-Name"));

    }


    @Test
    void testSetHeaderOptionalType() throws Exception {
        RuleEngine engine = new RuleEngine(ruleMap, null);
        String ruleId = "test-set-header-optional-code";
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("age", 30);
        Map<String, Object> result = engine.executeRule(ruleId, objMap);
        assertEquals(30, result.get("X-Test-Age"));
    }


    static class ClassA {
        String aname;
        ClassB bobject;
        public String getAname() {
            return aname;
        }
        public void setAname(String aname) {
            this.aname = aname;
        }
        public ClassB getBobject() {
            return bobject;
        }
        public void setBobject(ClassB bobject) {
            this.bobject = bobject;
        }
    }

    static class ClassB {
        String bname;
        ClassC cobject;
        public String getBname() {
            return bname;
        }
        public void setBname(String bname) {
            this.bname = bname;
        }
        public ClassC getCobject() {
            return cobject;
        }
        public void setCobject(ClassC cobject) {
            this.cobject = cobject;
        }

    }

    static class ClassC {
        String cname;
        int cint;
        Date cdate;

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public int getCint() {
            return cint;
        }

        public void setCint(int cint) {
            this.cint = cint;
        }

        public Date getCdate() {
            return cdate;
        }

        public void setCdate(Date cdate) {
            this.cdate = cdate;
        }

    }

}
