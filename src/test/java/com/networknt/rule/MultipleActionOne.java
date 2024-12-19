package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;

import java.util.Map;

public class MultipleActionOne implements IAction {
    @Override
    public void performAction(String ruleId, String actionId, Map<String, Object> inputMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
        resultMap.put("MultipleActionOne", true);
        System.out.println("MultipleActionOne performAction is called");
    }

}
