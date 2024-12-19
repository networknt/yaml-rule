package com.networknt.rule.operation;

import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerTypeOperation implements TypeSpecificOperation<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(IntegerTypeOperation.class);

    @Override
    public Integer convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        return Integer.valueOf(valueStr);
    }
    @Override
    public int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if(!(object instanceof Integer)) return 0;
        Object value = valueObject;
        if(valueObject instanceof String) {
            try {
                value = convert(ruleId, conditionId, object, (String)valueObject, null);
            } catch(Exception e) {
                String errorMsg = "value " + valueObject + " is not a number.";
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }

        }
        if(!(value instanceof Integer)) return 0;
        return ((Integer) object).compareTo((Integer) value);
    }

    @Override
    public int compareLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        return compare(ruleId, conditionId, object, valueObject) == 0;
    }
}
