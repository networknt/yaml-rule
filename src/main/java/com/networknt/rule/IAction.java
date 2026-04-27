package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;

import java.util.Collection;
import java.util.Map;


public interface IAction {
    /**
     * Action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param actionValues action values
     * @throws RuleEngineException exception thrown during performAction execution.
     */
    default void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) throws RuleEngineException {
        throw new UnsupportedOperationException("Action must implement collection or map actionValues");
    }

    default void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> actionValues) throws RuleEngineException {
        performAction(ruleId, actionId, objMap, resultMap, RuleAction.toActionValueCollection(actionValues));
    }

    /**
     * post action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param actionValues action values
     * @throws RuleEngineException exception thrown during postPerformAction execution.
     */
    default void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) throws RuleEngineException {
        // NOOP for classes implement IAction.
    };

    default void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> actionValues) throws RuleEngineException {
        postPerformAction(ruleId, actionId, objMap, resultMap, RuleAction.toActionValueCollection(actionValues));
    };
}
