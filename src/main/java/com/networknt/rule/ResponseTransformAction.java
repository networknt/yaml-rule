package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;

import java.util.Map;

/**
 * This is the interface for all response transformers that is used in light-4j ResponseTransformerInterceptor.
 * The interface will allow the interceptor to update the objMap from the resultMap so that several action
 * classes can be chained together to execute multiple plugins.
 *
 * @author Steve Hu
 */
public interface ResponseTransformAction extends TransformAction {
    String RESPONSE_BODY = "responseBody";

    default void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
        TransformAction.super.postPerformAction(ruleId, actionId, objMap, resultMap, parameters);
        // The previous action might change the responseBody, so let's replace it in the objMap from the resultMap
        // if it exists.
        if(resultMap.get(RESPONSE_BODY) != null) {
            objMap.put(RESPONSE_BODY, resultMap.get(RESPONSE_BODY));
        }
    }
}
