package com.networknt.rule.operation;

import com.networknt.rule.RuleConstants;
import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeOperation  implements TypeSpecificOperation<Date> {
    private static final Logger logger = LoggerFactory.getLogger(DateTypeOperation.class);

    @Override
    public Date convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        DateFormat df = null;
        if (dateFormat != null) {
            df = new SimpleDateFormat(dateFormat);
        } else {
            df = new SimpleDateFormat(RuleConstants.DEFAULT_DATE_FORMAT);
        }
        try {
            return  df.parse(valueStr);
        } catch (ParseException e) {
            String errorMsg = "Error parsing date string " + valueStr + " with format " + (dateFormat != null ? dateFormat : "yyyy-MM-dd HH:mm:ss");
            logger.error("Error parsing date string in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
    }
    @Override
    public int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {

        if (!(object instanceof java.util.Date)) {
            String errorMsg = "Object is not a Date";
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }

        if (equals(ruleId, conditionId, object, valueObject)) {
            return 0;
        }
        if (object == null && valueObject == null) {
            return 0;
        }
        if (object == null && valueObject != null) {
            return -1;
        }
        if (object != null && valueObject == null) {
            return 1;
        }

        Date value = null;
        if (valueObject instanceof String) {
            try {
                value = convert(ruleId, conditionId, object, (String)valueObject, null);
            } catch(Exception e) {
                String errorMsg = "value " + valueObject + " is not a date.";
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }
        } else if (valueObject instanceof Date) {
            value = (Date)valueObject;
        }


        if (value == null && object == null) {
            return 0;
        }
        if (value != null && object == null) {
            return -1;
        }
        if (value == null && object != null) {
            return 1;
        }

        if (object instanceof java.util.Date) {
            return ((java.util.Date) object).compareTo(value);
        }
        return 0;
    }

    @Override
    public int compareLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if(object == null || valueObject == null) return (object == null && valueObject == null);
        return object.equals(valueObject);
    }
}
