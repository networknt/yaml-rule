package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class RuleAction {
    private String actionId;
    private String actionDesc;
    private String actionClassName;
    private Boolean conditionResult;
    private Collection<RuleActionValue> actionValues;

    public RuleAction() {
    }

    public RuleAction(String actionId, String actionDesc, String actionClassName, Boolean conditionResult, Collection<RuleActionValue> actionValues) {
        this.actionId = actionId;
        this.actionDesc = actionDesc;
        this.actionClassName = actionClassName;
        this.conditionResult = conditionResult;
        this.actionValues = actionValues;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleAction that = (RuleAction) o;
        return conditionResult == that.conditionResult && Objects.equals(actionId, that.actionId) && Objects.equals(actionDesc, that.actionDesc) && Objects.equals(actionClassName, that.actionClassName) && Objects.equals(actionValues, that.actionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionDesc, actionClassName, conditionResult, actionValues);
    }
}
