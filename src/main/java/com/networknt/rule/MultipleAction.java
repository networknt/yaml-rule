package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

public class MultipleAction implements IAction {
    @Override
    public void performAction(Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) {
        System.out.println("MultipleAction performAction is called");
        resultMap.put("action1", true);
        resultMap.put("action2", true);
    }
}
