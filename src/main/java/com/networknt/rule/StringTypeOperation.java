package com.networknt.rule;

import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringTypeOperation implements TypeSpecificOperation<String> {
    private static final Logger logger = LoggerFactory.getLogger(StringTypeOperation.class);

    @Override
    public String convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        return valueStr;
    }
    @Override
    public int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if(object == null || valueObject == null) return 0;
        if(!(object instanceof String) || !(valueObject instanceof String)) return 0;
        return ((String)object).compareTo((String)valueObject);

    }

    @Override
    public int compareLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        if(object == null || valueObject == null) return 0;
        if(valueTypeCode.equals("STRING")) {
            if (!(valueObject instanceof String)) {
                valueObject = valueObject.toString();
            }
            if(object instanceof String) {
                return ((String)object).length() - ((String)valueObject).length();
            } else {
                return 0;
            }
        } else if(valueTypeCode.equals("INTEGER")) {
            if(object instanceof String) {
                return ((String)object).length() - Integer.valueOf(valueObject.toString());
            } else {
                return 0;
            }
        }  else {
            return 0;
        }

    }

    @Override
    public boolean equals(Object object, Object valueObject) throws RuleEngineException {
        if(object == null || valueObject == null) return (object == null && valueObject == null);
        return object.equals(valueObject);
    }
}
