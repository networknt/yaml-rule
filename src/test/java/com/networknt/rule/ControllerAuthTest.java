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
    public void testAuthWithCcTokenWithoutSid() throws Exception {
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
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
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
        pathParameters.put("service_name", serviceNameDeque);
        objMap.put("pathParameters", pathParameters);
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
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
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
    }

    @Test
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
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
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
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertTrue((Boolean)result.get(RuleConstants.RESULT));
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
        Map<String, Object> result = engine.executeRule("ccsid-group-role-auth", objMap);
        System.out.println("allowed = " + result.get(RuleConstants.RESULT));
        Assertions.assertFalse((Boolean)result.get(RuleConstants.RESULT));
    }

}
