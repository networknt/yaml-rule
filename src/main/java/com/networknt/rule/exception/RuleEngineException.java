package com.networknt.rule.exception;

public class RuleEngineException extends Exception {
    private final String ruleId;
    public RuleEngineException(String message, String ruleId) {
        super(message);
        this.ruleId = ruleId;
    }
    public String getRuleId() {
        return this.ruleId;
    }
}
