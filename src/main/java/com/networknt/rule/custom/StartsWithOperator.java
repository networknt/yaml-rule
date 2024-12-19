package com.networknt.rule.custom;

import com.networknt.rule.RuleConditionValue;
import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StartsWithOperator implements CustomOperator {
    private static final Logger logger = LoggerFactory.getLogger(StartsWithOperator.class);
    @Override
    public String getOperatorName() {
        return "startsWith";
    }

    @Override
    public boolean evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues) throws RuleEngineException {
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        }
        if (object instanceof String && valueObject instanceof String) {
            return ((String) object).startsWith((String) valueObject);
        } else {
            String errorMsg = "StartsWith evaluation with type different than string " + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            return false;
        }
    }
}
