package com.networknt.rule.exception;

public class ConditionEvaluationException extends RuleEngineException {
    private final String conditionId;
    public ConditionEvaluationException(String message, String ruleId, String conditionId) {
        super(message, ruleId);
        this.conditionId = conditionId;
    }
    public String getConditionId() {
        return this.conditionId;
    }
}
