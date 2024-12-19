package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class TestSetHeaderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(TestSetHeaderAction.class);
    @Override
    public void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Collection<RuleActionValue> actionValues) throws RuleEngineException {
        if(logger.isInfoEnabled()) logger.info("actionValues: {}", actionValues);
        for(RuleActionValue value: actionValues) {
            logger.info("setting header {} with value {}", value.getActionValueId(), value.getResolvedValue());
            resultMap.put(value.getActionValueId(), value.getResolvedValue());
        }
    }
}
