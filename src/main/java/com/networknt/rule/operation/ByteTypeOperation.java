package com.networknt.rule.operation;

import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteTypeOperation implements TypeSpecificOperation<Byte> {
    private static final Logger logger = LoggerFactory.getLogger(ByteTypeOperation.class);

    @Override
    public Byte convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        return Byte.valueOf(valueStr);
    }
    @Override
    public int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if(!(object instanceof Byte)) return 0;
        Object value = valueObject;
        if(valueObject instanceof String) {
            try {
                value = convert(ruleId, conditionId, object, (String)valueObject, null);
            } catch(Exception e) {
                String errorMsg = "value " + valueObject + " is not a Byte.";
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }

        }
        if(!(value instanceof Byte)) return 0;
        return ((Byte) object).compareTo((Byte) value);
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
