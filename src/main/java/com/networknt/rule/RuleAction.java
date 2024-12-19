package com.networknt.rule;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class RuleAction {
    private String actionId;
    private String actionDesc;
    private String actionClassName;
    private Boolean conditionResult;
    private Collection<RuleActionValue> actionValues;
    private Map<String, Object> parameters;

    public RuleAction() {
    }

    public RuleAction(String actionId, String actionDesc, String actionClassName, Boolean conditionResult, Collection<RuleActionValue> actionValues, Map<String, Object> parameters) {
        this.actionId = actionId;
        this.actionDesc = actionDesc;
        this.actionClassName = actionClassName;
        this.conditionResult = conditionResult;
        this.actionValues = actionValues;
        this.parameters = parameters;
    }

    public String getActionId() {
        return actionId;
    }
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public String getActionClassName() {
        return actionClassName;
    }

    public void setActionClassName(String actionClassName) {
        this.actionClassName = actionClassName;
    }

    public Boolean isConditionResult() {
        return conditionResult;
    }

    public void setConditionResult(Boolean conditionResult) {
        this.conditionResult = conditionResult;
    }

    public Collection<RuleActionValue> getActionValues() {
        return actionValues;
    }

    public void setActionValues(Collection<RuleActionValue> actionValues) {
        this.actionValues = actionValues;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleAction that = (RuleAction) o;
        return Objects.equals(actionId, that.actionId) && Objects.equals(actionDesc, that.actionDesc) && Objects.equals(actionClassName, that.actionClassName) && Objects.equals(conditionResult, that.conditionResult) && Objects.equals(actionValues, that.actionValues) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionDesc, actionClassName, conditionResult, actionValues, parameters);
    }
}
