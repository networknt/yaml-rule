package com.networknt.rule.exception;

public class PropertyNotFoundException extends RuleEngineException {
    private final String propertyPath;
    private final String conditionId;

    public PropertyNotFoundException(String message, String ruleId, String propertyPath, String conditionId) {
        super(message, ruleId);
        this.propertyPath = propertyPath;
        this.conditionId = conditionId;
    }

    public String getPropertyPath() {
        return this.propertyPath;
    }
    public String getConditionId() {
        return this.conditionId;
    }
}
