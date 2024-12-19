package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;

import java.util.Collection;
import java.util.Map;

public class MultipleActionTwo implements IAction {
    @Override
    public void performAction(String ruleId, String actionId, Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) throws RuleEngineException {
        resultMap.put("MultipleActionTwo", true);
        System.out.println("MultipleActionTwo performAction is called");
    }
}
