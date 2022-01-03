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


}
