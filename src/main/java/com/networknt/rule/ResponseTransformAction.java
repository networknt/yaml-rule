package com.networknt.rule;

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

    default void postPerformAction(Map<String, Object> objMap, Map<String, Object> resultMap) {
        TransformAction.super.postPerformAction(objMap, resultMap);
        // The previous action might change the responseBody, so let's replace it in the objMap from the resultMap
        // if it exists.
        if(resultMap.get(RESPONSE_BODY) != null) {
            objMap.put(RESPONSE_BODY, resultMap.get(RESPONSE_BODY));
        }
    }
}
