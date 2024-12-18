package com.networknt.rule;

import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoubleTypeOperation implements TypeSpecificOperation<Double> {
    private static final Logger logger = LoggerFactory.getLogger(DoubleTypeOperation.class);

    @Override
    public Double convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        return Double.valueOf(valueStr);
    }
    @Override
    public int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if(!(object instanceof Double)) return 0;
        Object value = valueObject;
        if(valueObject instanceof String) {
            try {
                value = convert(ruleId, conditionId, object, (String)valueObject, null);
            } catch(Exception e) {
                String errorMsg = "value " + valueObject + " is not a Double.";
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }
        }
        if(!(value instanceof Double)) return 0;
        return ((Double) object).compareTo((Double) value);
    }

    @Override
    public int compareLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(Object object, Object valueObject) throws RuleEngineException {
        if(object == null || valueObject == null) return (object == null && valueObject == null);
        return object.equals(valueObject);
    }
}
