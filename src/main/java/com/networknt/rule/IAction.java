package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import java.util.Map;


public interface IAction {
    /**
     * Action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param parameters configuration parameters including the result from the variable binding.
     * @throws RuleEngineException exception thrown during performAction execution.
     */
    void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException;

    /**
     * post action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param parameters configuration parameters including the result from the variable binding.
     * @throws RuleEngineException exception thrown during postPerformAction execution.
     */
    default void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
        // NOOP for classes implement IAction.
    };
}
