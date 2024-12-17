package com.networknt.rule;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ValidationAction implements IAction {

    public void performAction(Map<String, Object> objMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) {
        if(actionValues != null) {
            for (RuleActionValue rav : actionValues) {
                resultMap.put(rav.getActionValueId(), rav.getValue());
            }
        }
    }
}
