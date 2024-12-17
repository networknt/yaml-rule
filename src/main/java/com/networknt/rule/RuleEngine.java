package com.networknt.rule;

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
    public Map<String, Object> executeRules(String groupId, Map<String, Object> objMap) throws Exception {
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
                    handleActions(result, actions, objMap, resultMap);
                }
            } catch (Exception e) {
                logger.error("Rule Engine Runtime Exception", e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e);
            }
        } else {
            logger.error("Rule group cannot be found with groupId = {}", groupId);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule group not found for " + groupId);
        }
        return resultMap;
    }

    private void handleActions(boolean result, Collection<RuleAction> actions, Map<String, Object> objMap, Map<String, Object> resultMap) throws Exception {
        if(actions != null) {
            if(actions.size() == 1) {
                // if there is only one action, execute it only if the condition is true.
                RuleAction ra = actions.iterator().next();
                if(result) {
                    if (logger.isTraceEnabled())
                        logger.trace("Single action and evaluation is true, execute action  {}.", ra.getActionId());
                    performAction(ra, objMap, resultMap);
                } else {
                    if(logger.isTraceEnabled()) logger.trace("Single action and evaluation is false, skip action {}.", ra.getActionId());
                }
            } else {
                // if there are multiple actions, iterate all
                for (RuleAction ra : actions) {
                    if (ra.isConditionResult() == null) {
                        // if the condition is null, execute the action regardless.
                        if(logger.isTraceEnabled()) logger.trace("Multiple actions evaluation {}, conditionResult is {}, execute action {} regardless.", result, ra.isConditionResult(), ra.getActionId());
                        performAction(ra, objMap, resultMap);
                    } else {
                        // check the condition result.
                        if (ra.isConditionResult() == result) {
                            if(logger.isTraceEnabled()) logger.trace("Multiple actions evaluation is {}, conditionResult is {}, execute action {}.", result, ra.isConditionResult(), ra.getActionId());
                            performAction(ra, objMap, resultMap);
                        } else {
                            if (logger.isTraceEnabled()) logger.trace("Multiple actions evaluation is {}, conditionResult is {}, skip action {}.", result, ra.isConditionResult(), ra.getActionId());
                        }
                    }
                }
            }
        }
    }

    private void performAction(RuleAction ra, Map<String, Object> objMap, Map<String, Object> resultMap) throws Exception {
        String actionType = ra.getActionClassName();
        Collection<RuleActionValue> ravs = ra.getActionValues();
        // first check the cache to see if the action class is already loaded. If not, load it.
        // the RuleLoaderStartupHook will load all the action classes during server startup.
        IAction ia = actionClassCache.get(actionType);
        if (ia == null) {
            ia = (IAction) Class.forName(actionType).getDeclaredConstructor().newInstance();
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
    public Map<String, Object> executeRule(String ruleId, Map<String, Object> objMap) throws Exception {
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
                handleActions(result, actions, objMap, resultMap);
            } catch (Exception e ){
                logger.error("Rule Engine Runtime Exception", e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e);
            }
        } else {
            logger.error("Rule cannot be found with id = {}", ruleId);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule not found for "  + ruleId);
        }
        return resultMap;
    }
}
