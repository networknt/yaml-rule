package com.networknt.rule;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ValidationAction implements IAction {
    private static final Logger logger= LoggerFactory.getLogger(ValidationAction.class);
    @Override
    public void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap,Collection<RuleActionValue> actionValues) throws RuleEngineException {
        if(logger.isInfoEnabled()) logger.info("actionValues: {}", actionValues);
    }
}
