package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

public class MultipleActionThree implements IAction {
    @Override
    public void performAction(Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) {
        resultMap.put("MultipleActionThree", true);
        System.out.println("MultipleActionThree performAction is called");
    }
}
