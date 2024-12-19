package com.networknt.rule;

import com.networknt.rule.custom.CustomOperator;
import com.networknt.rule.exception.ActionExecutionException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleEngine {
    private static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);
    private final Map<String, Rule> ruleMap;
    private final Map<String, Collection<Rule>> groupMap;
    // cache for the rule action classes
    public final Map<String, IAction> actionClassCache = new ConcurrentHashMap<>();

    public RuleEngine(Map<String, Rule> ruleMap, Map<String, Collection<Rule>> groupMap) {
        this.ruleMap = ruleMap;
        this.groupMap = groupMap;
    }

    /**
     * Calls executeRules(objects, ruleId)
     */
    public Map<String, Object> executeRules(String groupId, Map<String, Object> objMap) throws RuleEngineException {
        Collection<Rule> rules = groupMap.get(groupId);
        Map<String, Object> resultMap = new HashMap<>();
        if(rules != null && !rules.isEmpty()) {
            // here we have a collection of rules
            try {
                for (Rule rule : rules) {
                    RuleEvaluator evaluator = RuleEvaluator.getInstance();
                    boolean result = evaluator.evaluate(rule, objMap, resultMap);
                    // save the evaluator result into the result map.
                    resultMap.put(RuleConstants.RESULT, result);
                    // trigger the action here.
                    Collection<RuleAction> actions = rule.getActions();
                    handleActions(rule.getRuleId(), result, actions, objMap, resultMap);
                }
            } catch (RuleEngineException e) {
                logger.error("Error executing rules in group {}: {}", groupId, e.getMessage(), e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e);
                throw e;
            }
        } else {
            String errorMsg = "Rule group cannot be found with groupId " + groupId;
            logger.error("Error executing rule group in group {}: {}", groupId, errorMsg);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule group not found for " + groupId);
            throw new RuleEngineException(errorMsg, groupId);
        }
        return resultMap;
    }

    private void handleActions(String ruleId, boolean result, Collection<RuleAction> actions, Map<String, Object> objMap, Map<String, Object> resultMap) throws RuleEngineException {
        if(actions != null) {
            if(actions.size() == 1) {
                // if there is only one action, execute it only if the condition is true.
                RuleAction ra = actions.iterator().next();
                if(result) {
                    if (logger.isTraceEnabled())
                        logger.trace("Single action and evaluation is true, execute action  {}.", ra.getActionId());
                    performAction(ruleId, ra, objMap, resultMap);
                } else {
                    if(logger.isTraceEnabled()) logger.trace("Single action and evaluation is false, skip action {}.", ra.getActionId());
                }
            } else {
                // if there are multiple actions, iterate all
                for (RuleAction ra : actions) {
                    if (ra.isConditionResult() == null) {
                        // if the condition is null, execute the action regardless.
                        if(logger.isTraceEnabled()) logger.trace("Multiple actions evaluation {}, conditionResult is {}, execute action {} regardless.", result, ra.isConditionResult(), ra.getActionId());
                        performAction(ruleId, ra, objMap, resultMap);
                    } else {
                        // check the condition result.
                        if (ra.isConditionResult() == result) {
                            if(logger.isTraceEnabled()) logger.trace("Multiple actions evaluation is {}, conditionResult is {}, execute action {}.", result, ra.isConditionResult(), ra.getActionId());
                            performAction(ruleId, ra, objMap, resultMap);
                        } else {
                            if (logger.isTraceEnabled()) logger.trace("Multiple actions evaluation is {}, conditionResult is {}, skip action {}.", result, ra.isConditionResult(), ra.getActionId());
                        }
                    }
                }
            }
        }
    }

    private void performAction(String ruleId, RuleAction ra, Map<String, Object> objMap, Map<String, Object> resultMap) throws RuleEngineException {
        String actionType = ra.getActionClassName();
        Collection<RuleActionValue> ravs = ra.getActionValues();
        // first check the cache to see if the action class is already loaded. If not, load it.
        // the RuleLoaderStartupHook will load all the action classes during server startup.
        IAction ia = actionClassCache.get(actionType);
        if (ia == null) {
            try {
                ia = (IAction) Class.forName(actionType).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                String errorMsg = "IAction class " + actionType + " not found";
                logger.error("Error executing action in rule {}, action {}: {}", ruleId, ra.getActionId(), errorMsg, e);
                throw new ActionExecutionException(errorMsg, ruleId, ra.getActionId());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | java.lang.reflect.InvocationTargetException e) {
                String errorMsg = "IAction class " + actionType + " cannot be initialized";
                logger.error("Error executing action in rule {}, action {}: {}", ruleId, ra.getActionId(), errorMsg, e);
                throw new ActionExecutionException(errorMsg, ruleId, ra.getActionId());
            }
            actionClassCache.put(actionType, ia);
        }
        ia.performAction(objMap, resultMap, ravs);
        // execute the post perform action.
        ia.postPerformAction(objMap, resultMap);
    }

    /**
     * Calls executeRule(ruleId, objects)
     *
     * @param ruleId rule id
     * @param objMap input map
     * @return result map
     */
    public Map<String, Object> executeRule(String ruleId, Map<String, Object> objMap) throws RuleEngineException {
        Rule rule = ruleMap.get(ruleId);
        Map<String, Object> resultMap = new HashMap<>();
        if(rule != null) {
            try {
                // rule must be in the map here. evaluate it.
                RuleEvaluator evaluator = RuleEvaluator.getInstance();
                boolean result = evaluator.evaluate(rule, objMap, resultMap);
                if(logger.isDebugEnabled()) logger.debug("executeRule result = {}", result);
                // save the evaluator result into the result map
                resultMap.put(RuleConstants.RESULT, result);
                // trigger the action here.
                Collection<RuleAction> actions = rule.getActions();
                handleActions(ruleId, result, actions, objMap, resultMap);
            } catch (RuleEngineException e ){
                logger.error("Error executing rule in rule {}: {}", ruleId, e.getMessage(), e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e.getMessage());
                throw e;
            }
        } else {
            String errorMsg = "Rule cannot be found with id " + ruleId;
            logger.error("Error executing rule in rule {}: {}", ruleId, errorMsg);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule not found for "  + ruleId);
            throw new RuleEngineException(errorMsg, ruleId);
        }
        return resultMap;
    }

    /**
     * Register a custom operator
     * @param operatorName The name of the operator that will be used in the rule definition.
     * @param operator The custom operator implementation.
     *
     */
    public void registerCustomOperator(String operatorName, CustomOperator operator) {
        RuleEvaluator.customOperatorRegistry.put(operatorName, operator);
    }

    public CustomOperator getCustomOperator(String operatorName) {
        return RuleEvaluator.customOperatorRegistry.get(operatorName);
    }
}
