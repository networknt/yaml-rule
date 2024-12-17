package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class RuleAction {
    private String actionId;
    private String actionClassName;
    private Collection<RuleActionValue> actionValues;

    public RuleAction() {
    }

    public RuleAction(String actionId, String actionClassName, Collection<RuleActionValue> actionValues) {
        this.actionId = actionId;
        this.actionClassName = actionClassName;
        this.actionValues = actionValues;
    }

    public String getActionId() {
        return actionId;
    }
Y
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionClassName() {
        return actionClassName;
    }

    public void setActionClassName(String actionClassName) {
        this.actionClassName = actionClassName;
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
        return Objects.equals(actionId, that.actionId) && Objects.equals(actionClassName, that.actionClassName) && Objects.equals(actionValues, that.actionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionClassName, actionValues);
    }
}
