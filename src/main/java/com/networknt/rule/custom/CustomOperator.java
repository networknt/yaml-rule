package com.networknt.rule.custom;

import com.networknt.rule.RuleConditionValue;
import com.networknt.rule.exception.RuleEngineException;

import java.util.List;

public interface CustomOperator {
    String getOperatorName();
    boolean evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues) throws RuleEngineException;
}
