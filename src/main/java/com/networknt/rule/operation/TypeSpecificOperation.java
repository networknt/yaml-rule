package com.networknt.rule.operation;

import com.networknt.rule.exception.RuleEngineException;

public interface TypeSpecificOperation<T> {
    T convert(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException;
    int compare(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException;
    int compareLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException;
    boolean equals(Object object, Object valueObject) throws RuleEngineException;
}
