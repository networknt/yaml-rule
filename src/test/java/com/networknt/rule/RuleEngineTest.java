package com.networknt.rule;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class RuleEngineTest {
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

        engine.executeRule("rule2", objMap);
    }

    @Test
    public void testRoleAuthWithCcToken() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1Mjc0MTUxNywianRpIjoiTzZfajltbnJibnZiSW16SVpkWk1EZyIsImlhdCI6MTYzNzM4MTUxNywibmJmIjoxNjM3MzgxMzk3LCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X2lkIjoiZjdkNDIzNDgtYzY0Ny00ZWZiLWE1MmQtNGM1Nzg3NDIxZTczIiwic2NvcGUiOiJwb3J0YWwuciBwb3J0YWwudyJ9.XODfxSsCWDFYzJNZj1azz8MS6w4jjAMlEagyPraGyZIuARXm-iIqFOF-feBxCJ2jAVf6fzWUQSf6pFCydESLWLfOn0eoFFlQjcREdz0rRT-z87D22z50s4eHP_W57pH67thHyrgLhWgB3glPFjPrmr_4mzJbmc9NtSS6zgN5fdyIrW94fghIxW6XCliFKlSpFPgi32QIrROseK-_1QJ3uTzlUzHVcQQsiWdKDrDWjXEjbd4Ny4qp_faL08GTLXkiYGy0Uw_7c5TnFMTSo5H7gEDMQtSTft_3UtNFr7gv0uK_HOfHa6rPQwfJCvy5-yDvzpmwX_xxHb9ozyJgpyhHeA";
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
        Map<String, Object> result = engine.executeRule("role-based-auth-skip-cc", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }


    @Test
    public void testRoleAuthSkipCcRightRole() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTYzNzM0MjgwMSwianRpIjoiQjRsTVRSSkJYN3JCZ05sMm5JRWcydyIsImlhdCI6MTYzNzM0MjIwMSwibmJmIjoxNjM3MzQyMDgxLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJyb2xlcyI6Im1hbmFnZXIgdGVsbGVyIiwic2NvcGUiOlsid3JpdGU6cGV0cyIsInJlYWQ6cGV0cyJdfQ.ADIoTbud9epDYJfoCEBkGEB60YbotwKZVDLXqJMmMxcxuwUm7wuPB0cLIqvDW18K4JKH6VyIy-L9lTWtP8ocpZxMFqgO0yR-d-wDrgNVmVok9G21rAWS1TbhzL7Gki4tZ6zn-dlwy9Iz1Zh2Opi4E01I99vFvmyuDkC354jC3CbMDHmi93AS9l6z39dP9lWWJKp8FO5ClK9DxHWLXG-qog5CdOt4u9k2_4zJ5dfOTkXYKt2vpl21ZIImLj14mJtr53AFZsONZCdTGoDsu-s-9xJ_c_4sgHlEgJ5-M46i2dgtKxeWzQe1yvA52Mbj3DU6HEgG-Q1As4zl42w1jJzoUQ";
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
        Map<String, Object> result = engine.executeRule("role-based-auth-skip-cc", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthSkipCcWrongRole() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTYzNzM0MjgwMSwianRpIjoiQjRsTVRSSkJYN3JCZ05sMm5JRWcydyIsImlhdCI6MTYzNzM0MjIwMSwibmJmIjoxNjM3MzQyMDgxLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJyb2xlcyI6Im1hbmFnZXIgdGVsbGVyIiwic2NvcGUiOlsid3JpdGU6cGV0cyIsInJlYWQ6cGV0cyJdfQ.ADIoTbud9epDYJfoCEBkGEB60YbotwKZVDLXqJMmMxcxuwUm7wuPB0cLIqvDW18K4JKH6VyIy-L9lTWtP8ocpZxMFqgO0yR-d-wDrgNVmVok9G21rAWS1TbhzL7Gki4tZ6zn-dlwy9Iz1Zh2Opi4E01I99vFvmyuDkC354jC3CbMDHmi93AS9l6z39dP9lWWJKp8FO5ClK9DxHWLXG-qog5CdOt4u9k2_4zJ5dfOTkXYKt2vpl21ZIImLj14mJtr53AFZsONZCdTGoDsu-s-9xJ_c_4sgHlEgJ5-M46i2dgtKxeWzQe1yvA52Mbj3DU6HEgG-Q1As4zl42w1jJzoUQ";
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
        objMap.put("roles", "user,customer");
        Map<String, Object> result = engine.executeRule("role-based-auth-skip-cc", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthWithRightRow() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTYzNzM0MjgwMSwianRpIjoiQjRsTVRSSkJYN3JCZ05sMm5JRWcydyIsImlhdCI6MTYzNzM0MjIwMSwibmJmIjoxNjM3MzQyMDgxLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJyb2xlcyI6Im1hbmFnZXIgdGVsbGVyIiwic2NvcGUiOlsid3JpdGU6cGV0cyIsInJlYWQ6cGV0cyJdfQ.ADIoTbud9epDYJfoCEBkGEB60YbotwKZVDLXqJMmMxcxuwUm7wuPB0cLIqvDW18K4JKH6VyIy-L9lTWtP8ocpZxMFqgO0yR-d-wDrgNVmVok9G21rAWS1TbhzL7Gki4tZ6zn-dlwy9Iz1Zh2Opi4E01I99vFvmyuDkC354jC3CbMDHmi93AS9l6z39dP9lWWJKp8FO5ClK9DxHWLXG-qog5CdOt4u9k2_4zJ5dfOTkXYKt2vpl21ZIImLj14mJtr53AFZsONZCdTGoDsu-s-9xJ_c_4sgHlEgJ5-M46i2dgtKxeWzQe1yvA52Mbj3DU6HEgG-Q1As4zl42w1jJzoUQ";
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
        Map<String, Object> result = engine.executeRule("role-based-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
    public void testRoleAuthWithWrongRow() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTYzNzM0MjgwMSwianRpIjoiQjRsTVRSSkJYN3JCZ05sMm5JRWcydyIsImlhdCI6MTYzNzM0MjIwMSwibmJmIjoxNjM3MzQyMDgxLCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzIiLCJyb2xlcyI6Im1hbmFnZXIgdGVsbGVyIiwic2NvcGUiOlsid3JpdGU6cGV0cyIsInJlYWQ6cGV0cyJdfQ.ADIoTbud9epDYJfoCEBkGEB60YbotwKZVDLXqJMmMxcxuwUm7wuPB0cLIqvDW18K4JKH6VyIy-L9lTWtP8ocpZxMFqgO0yR-d-wDrgNVmVok9G21rAWS1TbhzL7Gki4tZ6zn-dlwy9Iz1Zh2Opi4E01I99vFvmyuDkC354jC3CbMDHmi93AS9l6z39dP9lWWJKp8FO5ClK9DxHWLXG-qog5CdOt4u9k2_4zJ5dfOTkXYKt2vpl21ZIImLj14mJtr53AFZsONZCdTGoDsu-s-9xJ_c_4sgHlEgJ5-M46i2dgtKxeWzQe1yvA52Mbj3DU6HEgG-Q1As4zl42w1jJzoUQ";
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
        objMap.put("roles", "user, customer");
        Map<String, Object> result = engine.executeRule("role-based-auth", objMap);
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
        Assertions.assertTrue((Boolean)result.get("manager"));
        Assertions.assertTrue((Boolean)result.get("teller"));
    }

    @Test
    public void testGroup2RoleTransformManager() throws Exception {
        String groups = "admin";
        RuleEngine engine = new RuleEngine(ruleMap, null);
        Map objMap = new HashMap();
        objMap.put("groups", groups);
        Map<String, Object> result = engine.executeRule("petstore-group-role-transform", objMap);
        Assertions.assertTrue((Boolean)result.get("manager"));
        Assertions.assertFalse((Boolean)result.get("teller"));
    }

    class ClassA {
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

    class ClassB {
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

    class ClassC {
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
