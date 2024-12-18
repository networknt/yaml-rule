package com.networknt.rule.exception;

public class InvalidOperatorException extends RuleEngineException{
    private final String conditionId;
    private final String operatorCode;
    public InvalidOperatorException(String message, String ruleId, String conditionId, String operatorCode) {
        super(message, ruleId);
        this.conditionId = conditionId;
        this.operatorCode = operatorCode;
    }

    public String getConditionId() {
        return this.conditionId;
    }

    public String getOperatorCode() {
        return this.operatorCode;
    }
}
