package com.networknt.rule;

import com.networknt.rule.custom.CustomOperator;
import com.networknt.rule.exception.ActionExecutionException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleEngine {
    private static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);
    private final Map<String, Rule> ruleMap;
    private final Map<String, Collection<Rule>> groupMap;
    // cache for rule action implementations resolved by actionRef
    public final Map<String, IAction> actionClassCache = new ConcurrentHashMap<>();
    private final Map<String, IAction> actionRegistry = new ConcurrentHashMap<>();

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
        String actionType = ra.getActionRef();
        Collection<RuleActionValue> actionValues = ra.getActionValues();
        IAction ia = actionRegistry.get(actionType);
        try {
            if (ia == null) {
                // Compatibility fallback: existing Java deployments can keep using a fully-qualified
                // class name as the actionRef until they register a neutral action name.
                ia = actionClassCache.computeIfAbsent(actionType, key -> {
                    try {
                        return (IAction) Class.forName(key).getDeclaredConstructor().newInstance();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | java.lang.reflect.InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ClassNotFoundException) {
                String errorMsg = "IAction actionRef " + actionType + " not found";
                logger.error("Error executing action in rule {}, action {}: {}", ruleId, ra.getActionId(), errorMsg, cause);
                throw new ActionExecutionException(errorMsg, ruleId, ra.getActionId());
            } else if (cause instanceof InstantiationException || cause instanceof IllegalAccessException
                    || cause instanceof NoSuchMethodException || cause instanceof java.lang.reflect.InvocationTargetException) {
                String errorMsg = "IAction actionRef " + actionType + " cannot be initialized";
                logger.error("Error executing action in rule {}, action {}: {}", ruleId, ra.getActionId(), errorMsg, cause);
                throw new ActionExecutionException(errorMsg, ruleId, ra.getActionId());
            } else {
                throw e;
            }
        }
        Collection<RuleActionValue> clonedValues = cloneAndResolveActionValues(actionValues, objMap, resultMap);
        Map<String, Object> actionValueMap = ra.getActionValueMap();
        if (actionValueMap != null) {
            Map<String, Object> resolvedActionValueMap = resolveActionValueMap(actionValueMap, objMap, resultMap);
            ia.performAction(ruleId, ra.getActionId(), objMap, resultMap, resolvedActionValueMap);
            ia.postPerformAction(ruleId, ra.getActionId(), objMap, resultMap, resolvedActionValueMap);
        } else {
            ia.performAction(ruleId, ra.getActionId(), objMap, resultMap, clonedValues);
            ia.postPerformAction(ruleId, ra.getActionId(), objMap, resultMap, clonedValues);
        }
    }

    private Collection<RuleActionValue> cloneAndResolveActionValues(Collection<RuleActionValue> actionValues, Map<String, Object> objMap, Map<String, Object> resultMap) throws RuleEngineException {
        Collection<RuleActionValue> clonedValues = null;
        if(actionValues != null) {
            clonedValues = new ArrayList<>(actionValues.size());
            for (RuleActionValue actionValue: actionValues) {
                RuleActionValue clonedValue = new RuleActionValue(actionValue);
                clonedValue.setResolvedValue(RuleEvaluator.getInstance().resolveVariable(clonedValue.getValue(), objMap, resultMap));
                clonedValues.add(clonedValue);
            }
        }
        return clonedValues;
    }

    private Map<String, Object> resolveActionValueMap(Map<String, Object> actionValues, Map<String, Object> objMap, Map<String, Object> resultMap) throws RuleEngineException {
        Map<String, Object> resolvedValues = new HashMap<>();
        for (Map.Entry<String, Object> entry : actionValues.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                value = RuleEvaluator.getInstance().resolveVariable((String)value, objMap, resultMap);
            }
            resolvedValues.put(entry.getKey(), value);
        }
        return resolvedValues;
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

    public void registerAction(String actionRef, IAction action) {
        actionRegistry.put(actionRef, action);
    }

    public CustomOperator getCustomOperator(String operatorName) {
        return RuleEvaluator.customOperatorRegistry.get(operatorName);
    }
}
