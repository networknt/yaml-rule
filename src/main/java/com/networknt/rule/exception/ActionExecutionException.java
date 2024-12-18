package com.networknt.rule.exception;

public class ActionExecutionException extends RuleEngineException {
    private final String actionId;
    public ActionExecutionException(String message, String ruleId, String actionId) {
        super(message, ruleId);
        this.actionId = actionId;
    }
    public String getConditionId() {
        return this.actionId;
    }
}
