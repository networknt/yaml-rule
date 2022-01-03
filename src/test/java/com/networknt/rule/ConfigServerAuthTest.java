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

public class ConfigServerAuthTest {
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
     * The token is client credentials, and it has a sid claim to access the config server, and the service_name in the path
     * parameter matches the jwt sid claim. The result should be true.
     *
     * @throws Exception
     */
    @Test
    public void testAuthWithCcTokenWithSid() throws Exception {
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
        objMap.put("roles", "manager,teller");
        // pass in the pathParameters so that the sid can be matched with the service in the path parameter.
        Deque<String> serviceNameDeque = new ArrayDeque<String>();
        serviceNameDeque.add("0100");
        Map<String, Deque<String>> pathParameters = new HashMap<>();
        pathParameters.put("service-name", serviceNameDeque);
        objMap.put("pathParameters", pathParameters);
        Map<String, Object> result = engine.executeRule("config-server-access", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

}
