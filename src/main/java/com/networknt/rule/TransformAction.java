package com.networknt.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TransformAction extends IAction {
    String REQUEST_HEADERS = "requestHeaders";
    String RESPONSE_HEADERS = "responseHeaders";
    String REMOVE = "remove";
    String UPDATE = "update";

    default void removeRequestHeader(Map<String, Object> resultMap, String headerName) {
        Map<String, Object> requestHeaders = (Map)resultMap.get(REQUEST_HEADERS);
        if(requestHeaders == null) {
            requestHeaders = new HashMap<>();
            resultMap.put(REQUEST_HEADERS, requestHeaders);
        }
        List<String> removeList = (List<String>)requestHeaders.get(REMOVE);
        if(removeList == null) {
            removeList = new ArrayList<>();
            requestHeaders.put(REMOVE, removeList);
        }
        removeList.add(headerName);
    }

    default void updateRequestHeader(Map<String, Object> resultMap, String headerName, String headerValue) {
        Map<String, Object> requestHeaders = (Map)resultMap.get(REQUEST_HEADERS);
        if(requestHeaders == null) {
            requestHeaders = new HashMap<>();
            resultMap.put(REQUEST_HEADERS, requestHeaders);
        }
        Map<String, String> updateMap = (Map<String, String>)requestHeaders.get(UPDATE);
        if(updateMap == null) {
            updateMap = new HashMap<>();
            requestHeaders.put(UPDATE, updateMap);
        }
        updateMap.put(headerName, headerValue);
    }

    default void removeResponseHeader(Map<String, Object> resultMap, String headerName) {
        Map<String, Object> responseHeaders = (Map)resultMap.get(RESPONSE_HEADERS);
        if(responseHeaders == null) {
            responseHeaders = new HashMap<String, Object>();
            resultMap.put(RESPONSE_HEADERS, responseHeaders);
        }
        List<String> removeList = (List<String>)responseHeaders.get(REMOVE);
        if(removeList == null) {
            removeList = new ArrayList<String>();
            responseHeaders.put(REMOVE, removeList);
        }
        removeList.add(headerName);

    }

    default void updateResponseHeader(Map<String, Object> resultMap, String headerName, String headerValue) {
        Map<String, Object> responseHeaders = (Map)resultMap.get(RESPONSE_HEADERS);
        if(responseHeaders == null) {
            responseHeaders = new HashMap<String, Object>();
            resultMap.put(RESPONSE_HEADERS, responseHeaders);
        }
        Map<String, String> updateMap = (Map<String, String>)responseHeaders.get(UPDATE);
        if(updateMap == null) {
            updateMap = new HashMap<String, String>();
            responseHeaders.put(UPDATE, updateMap);
        }
        updateMap.put(headerName, headerValue);

    }

}
