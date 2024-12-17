package com.networknt.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    public Map executeRules(String groupId, Map<String, Object> objMap) throws Exception {
        Collection<Rule> rules = groupMap.get(groupId);
        Map<String, Object> resultMap = new HashMap();
        if(rules != null && rules.size() > 0) {
            // here we have a collection of rules
            try {
                Iterator itRules = rules.iterator();
                while(itRules.hasNext()) {
                    Rule rule = (Rule)itRules.next();
                    RuleEvaluator evaluator = RuleEvaluator.getInstance();
                    boolean result = evaluator.evaluate(rule, objMap, resultMap);
                    // save the evaluator result into the result map.
                    resultMap.put(RuleConstants.RESULT, Boolean.valueOf(result));
                    // trigger the action here.
                    Iterator itActions = rule.getActions().iterator();
                    while(itActions.hasNext()) {
                        RuleAction ra = (RuleAction)itActions.next();
                        // check the condition result.
                        if(ra.isConditionResult() == result || !ra.isConditionResult()) {
                            String actionType = ra.getActionClassName();
                            Collection ravs = ra.getActionValues();
                            // first check the cache to see if the action class is already loaded. If not, load it.
                            // the RuleLoaderStartupHook will load all the action classes during server startup.
                            IAction ia = actionClassCache.get(actionType);
                            if(ia == null) {
                                ia = (IAction)Class.forName(actionType).getDeclaredConstructor().newInstance();
                                actionClassCache.put(actionType, ia);
                            }
                            ia.performAction(objMap, resultMap, ravs);
                            // execute the post perform action.
                            ia.postPerformAction(objMap, resultMap);
                        }
                    }
                    if(!result) {
                        // evaluator result is false, break and return the false.
                        break;
                    }
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

    public Map executeRule(String ruleId, Map<String, Object> objMap) throws Exception {
        Rule rule = ruleMap.get(ruleId);
        Map<String, Object> resultMap = new HashMap();
        if(rule != null) {
            try {
                // rule must be in the map here. evaluate it.
                RuleEvaluator evaluator = RuleEvaluator.getInstance();
                boolean result = evaluator.evaluate(rule, objMap, resultMap);
                if(logger.isDebugEnabled()) logger.debug("executeRule result = " + result);
                // save the evaluator result into the result map
                resultMap.put(RuleConstants.RESULT, Boolean.valueOf(result));
                // trigger the action here.
                Collection actions = rule.getActions();
                if(actions != null) {
                    Iterator it = actions.iterator();
                    while(it.hasNext()) {
                        RuleAction ra = (RuleAction)it.next();
                        // check the condition result.
                        if(ra.isConditionResult() == result || !ra.isConditionResult()) {
                            String actionType = ra.getActionClassName();
                            Collection<RuleActionValue> ravs = ra.getActionValues();
                            // first check the cache to see if the action class is already loaded. If not, load it.
                            // the RuleLoaderStartupHook will load all the action classes during server startup.
                            IAction ia = actionClassCache.get(actionType);
                            if(ia == null) {
                                ia = (IAction)Class.forName(actionType).getDeclaredConstructor().newInstance();
                                actionClassCache.put(actionType, ia);
                            }
                            ia.performAction(objMap, resultMap, ravs);
                            // execute the post perform action.
                            ia.postPerformAction(objMap, resultMap);
                        }
                    }
                }
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
