package com.networknt.rule;

import java.util.Map;

/**
 * This is the interface for all request transformers that is used in light-4j RequestTransformerInterceptor.
 * The interface will allow the interceptor to update the objMap from the resultMap so that several action
 * classes can be chained together to execute multiple plugins.
 *
 * @author Steve Hu
 */
public interface RequestTransformAction extends TransformAction {
    String REQUEST_BODY = "requestBody";

    @Override
    default void postPerformAction(Map<String, Object> objMap, Map<String, Object> resultMap) {
        TransformAction.super.postPerformAction(objMap, resultMap);
        // The previous action might change the requestBody, so let's replace it in the objMap from the resultMap
        // if it exists.
        if(resultMap.get(REQUEST_BODY) != null) {
            objMap.put(REQUEST_BODY, resultMap.get(REQUEST_BODY));
        }
        // previous action updates the headers with update map and remove list.

    }
}