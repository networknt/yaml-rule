package com.networknt.rule;

import org.jose4j.jwt.JwtClaims;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class FineGrainedAuthAction implements IAction {
    public void performAction(Map objMap, Map resultMap, Collection actionValues) {
        resultMap.put(RuleConstants.RESULT, false);
        // when this action is called, we either have a client credentials token or
        // an authorization code token with roles available.
        Object allowCcObj = resultMap.get("allow-cc");
        if(allowCcObj != null && ((Boolean)allowCcObj)) {
            resultMap.put(RuleConstants.RESULT, true);
        } else {
            Iterator it = actionValues.iterator();
            while(it.hasNext()) {
                RuleActionValue rav = (RuleActionValue)it.next();
                if("roles".equals(rav.getActionValueId())) {
                    String v = rav.getValue();
                    if (v != null) {
                        if(v.startsWith("$")) {
                            v = (String)objMap.get(v.substring(1));
                        }
                        // match the jwt roles with v required by the endpoint.
                        Map<String, Object> auditInfo = (Map)objMap.get("auditInfo");
                        JwtClaims jwtClaims = (JwtClaims) auditInfo.get("subject_claims");
                        String roles = (String)jwtClaims.getClaimValue("roles");
                        System.out.println("roles = " + roles + " v = " + v);
                        String[] split = roles.split("\\s+");
                        for(String s: split) {
                            if(v.indexOf(s) >= 0) {
                                resultMap.put(RuleConstants.RESULT, true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
