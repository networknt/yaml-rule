package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

public class MultipleActionTwo implements IAction {
    @Override
    public void performAction(Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) {
        System.out.println("MultipleActionTwo performAction is called");
    }
}
