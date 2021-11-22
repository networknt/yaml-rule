package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

public interface IAction {
    /**
     * The performAction is call while evaluate result is true. It takes the input
     * Map and return another Map as result of rule engine execution.
     * @param inputMap input map
     * @param resultMap result map
     * @param actionValues action values
     */
    void performAction(Map<String, Object> inputMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues);
}
