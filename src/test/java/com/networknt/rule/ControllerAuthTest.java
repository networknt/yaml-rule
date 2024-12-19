package com.networknt.rule;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerAuthTest {
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

    /**
     * The token is client credentials, and it doesn't have a sid claim to access the controller and the result
     * should be false.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterWithCcTokenWithoutSid() throws Exception {
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
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("serviceId", "0100");
        objMap.put("requestBody", bodyMap);
        Map<String, Object> result = engine.executeRule("controller-register-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is client credentials, and it has a sid claim to access the controller and the result
     * should be true.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterWithCcTokenWithSid() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1NjI2MjgwOSwianRpIjoieGN1VXdOeHNseFRrVDZCQXR0WTdNdyIsImlhdCI6MTY0MDkwMjgwOSwibmJmIjoxNjQwOTAyNjg5LCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X2lkIjoiZjdkNDIzNDgtYzY0Ny00ZWZiLWE1MmQtNGM1Nzg3NDIxZTcyIiwic2NvcGUiOiJwb3J0YWwuciBwb3J0YWwudyIsInNpZCI6IjAxMDAifQ.W5oRWYy6uLGkXysQbekdlGc92kaW1OXq_eGpoEcWbw13xd9bpAid0aiHxXTvOh1vArce2lZ46K1Zcpo0vF456_O4qmwX-dyAqqcWlca9MG5Juo1oh1-QYMKfm3lpj2VeqpQ4LSF2C3LvX6zRjCvM7_2S9i12pBu7-oyJp9QZsYc1tmp5PK7EoFwSLUUk28SiEuY_gtmUkCHMakwLYkOK-4XQyMbFfLB-Ft-ZD2FC8UNvjNFGBCXUyCL071AZ7DpmOela4uNMLChBVZIxYULFEQVwA5c8jQwxa3uXr8-mg9S36jziRYf-CBZu51ig8vI1yphcyqRWruqJ_7A3XW0AFQ";
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
        // pass in the requestBody so that the sid can be matched with the serviceId in the request body.
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("serviceId", "0100");
        objMap.put("requestBody", bodyMap);
        Map<String, Object> result = engine.executeRule("controller-register-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is client credentials, and it doesn't have a sid claim to access the controller and the result
     * should be false.
     *
     * @throws Exception
     */
    @Test
    public void testDeregisterWithCcTokenWithoutSid() throws Exception {
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
        Deque<String> serviceIdDeque = new ArrayDeque<String>();
        serviceIdDeque.add("0100");
        Map<String, Deque<String>> queryParameters = new HashMap<>();
        queryParameters.put("serviceId", serviceIdDeque);
        objMap.put("queryParameters", queryParameters);
        Map<String, Object> result = engine.executeRule("controller-deregister-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is client credentials, and it has a sid claim to access the controller and the result
     * should be true.
     *
     * @throws Exception
     */
    @Test
    public void testDeregisterWithCcTokenWithSid() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1NjI2MjgwOSwianRpIjoieGN1VXdOeHNseFRrVDZCQXR0WTdNdyIsImlhdCI6MTY0MDkwMjgwOSwibmJmIjoxNjQwOTAyNjg5LCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X2lkIjoiZjdkNDIzNDgtYzY0Ny00ZWZiLWE1MmQtNGM1Nzg3NDIxZTcyIiwic2NvcGUiOiJwb3J0YWwuciBwb3J0YWwudyIsInNpZCI6IjAxMDAifQ.W5oRWYy6uLGkXysQbekdlGc92kaW1OXq_eGpoEcWbw13xd9bpAid0aiHxXTvOh1vArce2lZ46K1Zcpo0vF456_O4qmwX-dyAqqcWlca9MG5Juo1oh1-QYMKfm3lpj2VeqpQ4LSF2C3LvX6zRjCvM7_2S9i12pBu7-oyJp9QZsYc1tmp5PK7EoFwSLUUk28SiEuY_gtmUkCHMakwLYkOK-4XQyMbFfLB-Ft-ZD2FC8UNvjNFGBCXUyCL071AZ7DpmOela4uNMLChBVZIxYULFEQVwA5c8jQwxa3uXr8-mg9S36jziRYf-CBZu51ig8vI1yphcyqRWruqJ_7A3XW0AFQ";
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
        // pass in the queryParameters so that the sid can be matched with the serviceId in the query parameter.
        Deque<String> serviceIdDeque = new ArrayDeque<String>();
        serviceIdDeque.add("0100");
        Map<String, Deque<String>> queryParameters = new HashMap<>();
        queryParameters.put("serviceId", serviceIdDeque);
        objMap.put("queryParameters", queryParameters);
        Map<String, Object> result = engine.executeRule("controller-deregister-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is an authorization code flow token with a list of roles as custom claims. The portal-role-access rule
     * will be called and the allowed should be true with the right roles in the claim.
     *
     * @throws Exception
     */
    @Test
    public void testRoleAccessRightRoles() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk2MDQwMzU5OSwianRpIjoiWGUtZDJLbjhlNkROREV3UWtvRUZTUSIsImlhdCI6MTY0NTA0MzU5OSwibmJmIjoxNjQ1MDQzNDc5LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHVAZ21haWwuY29tIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzMiLCJyb2xlcyI6InVzZXIgQ3RsUGx0QWRtaW4gQ3RsUGx0UmVhZCBDdGxQbHRXcml0ZSIsInNjb3BlIjpbInBvcnRhbC5yIiwicG9ydGFsLnciXX0.eXE7dVBPsfXgTfKdz-SjLF8h2nh3bYW53hGMXRTBfGYAQBBP5rnn3OZ_Pd4qd4juai9j-mHmMW9rLxqgfIxYZ1bNcf86GjGgNJ6ynBD5WfioUhk6dfyWk3n912pkGaxVfYxixauLpQmY6_ysRYYLrp945Cih4CDKjrr7yDNcKnLyuyEMzLUWFqZOnxdg3Qa2KuMv517AZD1zTn3GN-d4H4M0PxL5SwRDd28PcXWYcUUu_u0DdWYFIU6uzKC1WPsrqE565k6_viPziVi7yhrrloJE6YRIjBy_Qp8s4LOg69KUPv19Bvgj66c2_IDB5JojHYaj-KJPzdIEn_Ttf_teJA";
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
        objMap.put("roles", "CtlPltAdmin,CtlPltRead,CtlAppAdmin,CtlAppRead");
        // pass in the pathParameters so that the sid can be matched with the service in the path parameter.
        Map<String, Object> result = engine.executeRule("portal-role-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is an authorization code flow token with a list of roles as custom claims. The portal-role-access rule
     * will be called and the allowed should be true with the right roles in the claim.
     *
     * To make is simple, we change the list of the roles in the rule input and keep the same token as the above test.
     *
     * @throws Exception
     */
    @Test
    @Disabled
    public void testRoleAccessWrongRoles() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk2MDQwMzU5OSwianRpIjoiWGUtZDJLbjhlNkROREV3UWtvRUZTUSIsImlhdCI6MTY0NTA0MzU5OSwibmJmIjoxNjQ1MDQzNDc5LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHVAZ21haWwuY29tIiwidXNlcl90eXBlIjoiRU1QTE9ZRUUiLCJjbGllbnRfaWQiOiJmN2Q0MjM0OC1jNjQ3LTRlZmItYTUyZC00YzU3ODc0MjFlNzMiLCJyb2xlcyI6InVzZXIgQ3RsUGx0QWRtaW4gQ3RsUGx0UmVhZCBDdGxQbHRXcml0ZSIsInNjb3BlIjpbInBvcnRhbC5yIiwicG9ydGFsLnciXX0.eXE7dVBPsfXgTfKdz-SjLF8h2nh3bYW53hGMXRTBfGYAQBBP5rnn3OZ_Pd4qd4juai9j-mHmMW9rLxqgfIxYZ1bNcf86GjGgNJ6ynBD5WfioUhk6dfyWk3n912pkGaxVfYxixauLpQmY6_ysRYYLrp945Cih4CDKjrr7yDNcKnLyuyEMzLUWFqZOnxdg3Qa2KuMv517AZD1zTn3GN-d4H4M0PxL5SwRDd28PcXWYcUUu_u0DdWYFIU6uzKC1WPsrqE565k6_viPziVi7yhrrloJE6YRIjBy_Qp8s4LOg69KUPv19Bvgj66c2_IDB5JojHYaj-KJPzdIEn_Ttf_teJA";
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
        objMap.put("roles", "CfgPltAdmin,CfgPltRead,CfgAppAdmin,CfgAppRead");
        // pass in the pathParameters so that the sid can be matched with the service in the path parameter.
        Map<String, Object> result = engine.executeRule("portal-role-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is an authorization code flow token with a list of groups as custom claims. In this case, we need
     * to call the group to role transform rule first and then call the portal-role-access rule to match the roles.
     * The jwt token has group User_API_Dev_R, User_API_Dev_W, User_API_Dev_A
     *
     * @throws Exception
     */
    @Test
    public void testRoleAccessRightGroups() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk2MDQwNDcwNCwianRpIjoibzVSeUFCcGk3ckE4MS1lZk5IX3RPQSIsImlhdCI6MTY0NTA0NDcwNCwibmJmIjoxNjQ1MDQ0NTg0LCJ2ZXJzaW9uIjoiMS4wIiwidXNlcl9pZCI6InN0ZXZlaHUiLCJ1c2VyX3R5cGUiOiJFTVBMT1lFRSIsImNsaWVudF9pZCI6ImY3ZDQyMzQ4LWM2NDctNGVmYi1hNTJkLTRjNTc4NzQyMWU3MiIsImdyb3VwcyI6IlVzZXJfQVBJX0Rldl9SIFVzZXJfQVBJX0Rldl9XIiwic2NvcGUiOlsicG9ydGFsLnIiLCJwb3J0YWwudyJdfQ.aZh3j9YciSPIAw5IIsWnpEh5H4bGFSX76iDlqV1GnK-C3fSc4jaivn6Mfx20bNvj-qGuDKY7e_wAwfRY-cHuzZMMhhFb4m7_ykg0YB2NpaGA8OqVtcTmWOhVQ_JVyWruWTjVxHX2YchPZWPAsbErDicE__1psuouxO2Hz8p0d3CKVX3gGKiPzOGAvd2d7bp8LQu1-luMblJfreUS_txqsvEC49m0srYy5E5FIy6e0h5WSZmAlzxR9cHp8X2I6lrctMLIHO-ljD6uFfOZHYQJ6LZu_NSVCvagOx-n_1DBYM1Gxh57aDIIGrVFKBHzTeUYVAAN4W6aOsL4hH-QYWRzlw";
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
        // this is roles passed to the rule in the endpoint rule definition page. If multiple roles, they
        // can be separated by comma or space.
        objMap.put("roles", "CtlPltAdmin CtlPltRead CtlAppAdmin CtlAppRead");
        // pass in the pathParameters so that the sid can be matched with the service in the path parameter.
        Map<String, Object> result = engine.executeRule("controller-group-role", objMap);
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));

        result = engine.executeRule("portal-role-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }


    /**
     * The token is client credentials, and it doesn't have a sid claim to access the controller and the result
     * should be false.
     *
     * @throws Exception
     */
    @Test
    public void testCheckWithCcTokenWithoutSid() throws Exception {
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
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", "0100:192.168.1.10:8443");
        objMap.put("requestBody", bodyMap);
        Map<String, Object> result = engine.executeRule("controller-check-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

    /**
     * The token is client credentials, and it has a sid claim to access the controller and the result
     * should be true.
     *
     * @throws Exception
     */
    @Test
    public void testCheckWithCcTokenWithSid() throws Exception {
        String jwt = "eyJraWQiOiIxMDAiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ1cm46Y29tOm5ldHdvcmtudDpvYXV0aDI6djEiLCJhdWQiOiJ1cm46Y29tLm5ldHdvcmtudCIsImV4cCI6MTk1NjI2MjgwOSwianRpIjoieGN1VXdOeHNseFRrVDZCQXR0WTdNdyIsImlhdCI6MTY0MDkwMjgwOSwibmJmIjoxNjQwOTAyNjg5LCJ2ZXJzaW9uIjoiMS4wIiwiY2xpZW50X2lkIjoiZjdkNDIzNDgtYzY0Ny00ZWZiLWE1MmQtNGM1Nzg3NDIxZTcyIiwic2NvcGUiOiJwb3J0YWwuciBwb3J0YWwudyIsInNpZCI6IjAxMDAifQ.W5oRWYy6uLGkXysQbekdlGc92kaW1OXq_eGpoEcWbw13xd9bpAid0aiHxXTvOh1vArce2lZ46K1Zcpo0vF456_O4qmwX-dyAqqcWlca9MG5Juo1oh1-QYMKfm3lpj2VeqpQ4LSF2C3LvX6zRjCvM7_2S9i12pBu7-oyJp9QZsYc1tmp5PK7EoFwSLUUk28SiEuY_gtmUkCHMakwLYkOK-4XQyMbFfLB-Ft-ZD2FC8UNvjNFGBCXUyCL071AZ7DpmOela4uNMLChBVZIxYULFEQVwA5c8jQwxa3uXr8-mg9S36jziRYf-CBZu51ig8vI1yphcyqRWruqJ_7A3XW0AFQ";
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
        // pass in the requestBody so that the sid can be matched with the serviceId in the request body.
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", "0100:192.168.1.10:8443");
        objMap.put("requestBody", bodyMap);
        Map<String, Object> result = engine.executeRule("controller-check-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }


}
