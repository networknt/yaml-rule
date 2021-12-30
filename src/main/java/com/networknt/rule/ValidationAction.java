package com.networknt.rule;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ValidationAction implements IAction {

    public void performAction(Map objMap, Map resultMap, Collection actionValues) {
        if(actionValues != null) {
            Iterator it = actionValues.iterator();
            while(it.hasNext()) {
                RuleActionValue rav = (RuleActionValue)it.next();
                resultMap.put(rav.getActionValueId(), rav.getValue());
            }
        }
    }
}
