package com.networknt.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RuleEngine {
    private static Logger logger = LoggerFactory.getLogger(RuleEngine.class);

    private Map<String, Rule> ruleMap;
    private Map<String, Collection<Rule>> groupMap;

    public RuleEngine(Map<String, Rule> ruleMap, Map<String, Collection<Rule>> groupMap) {
        this.ruleMap = ruleMap;
        this.groupMap = groupMap;
    }

    /**
     * Calls executeRules(objects, ruleId)
     */
    public Map executeRules(String groupId, Map objMap) throws Exception {
        Collection<Rule> rules = groupMap.get(groupId);
        Map resultMap = new HashMap();
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
                    if(result == true) {
                        // trigger the action here.
                        Iterator itActions = rule.getActions().iterator();
                        while(itActions.hasNext()) {
                            RuleAction ra = (RuleAction)itActions.next();
                            String actionType = ra.getActionClassName();
                            Collection ravs = ra.getActionValues();
                            IAction ia = (IAction)Class.forName(actionType).getDeclaredConstructor().newInstance();
                            ia.performAction(objMap, resultMap, ravs);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Rule Engine Runtime Exception", e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e);
            }
        } else {
            logger.error("Rule group cannot be found with groupId = " + groupId);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule group not found for " + groupId);
        }
        return resultMap;
    }

    public Map executeRule(String ruleId, Map objMap) throws Exception {
        Rule rule = ruleMap.get(ruleId);
        Map resultMap = new HashMap();
        if(rule != null) {
            try {
                // rule must be in the map here. evaluate it.
                RuleEvaluator evaluator = RuleEvaluator.getInstance();
                boolean result = evaluator.evaluate(rule, objMap, resultMap);
                System.out.println("executeRule result = " + result);
                // save the evaluator result into the result map
                resultMap.put(RuleConstants.RESULT, Boolean.valueOf(result));
                if(result == true) {
                    // trigger the action here.
                    Iterator it = rule.getActions().iterator();
                    while(it.hasNext()) {
                        RuleAction ra = (RuleAction)it.next();
                        String actionType = ra.getActionClassName();
                        Collection<RuleActionValue> ravs = ra.getActionValues();
                        IAction ia = (IAction)Class.forName(actionType).getDeclaredConstructor().newInstance();
                        ia.performAction(objMap, resultMap, ravs);
                    }
                }
            } catch (Exception e ){
                logger.error("Rule Engine Runtime Exception", e);
                resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, e);
            }
        } else {
            logger.error("Rule cannot be found with id = " + ruleId);
            resultMap.put(RuleConstants.RULE_ENGINE_EXCEPTION, "Rule not found for "  + ruleId);
        }
        return resultMap;
    }
}
