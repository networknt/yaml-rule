package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class TestSetHeaderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(TestSetHeaderAction.class);
    @Override
    public void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
        String headerName = (String) parameters.get("headerName");
        Object headerValue =  parameters.get("headerValue");

        if(headerName != null && headerValue != null) {
            // set header using headerName and headerValue
            logger.info("setting header " + headerName + " with value " + headerValue);
            resultMap.put(headerName, headerValue);
        }

    }
}
