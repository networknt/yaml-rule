package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

public class MultipleActionOne implements IAction {
    @Override
    public void performAction(Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) {
        System.out.println("MultipleActionOne performAction is called");
    }
}
