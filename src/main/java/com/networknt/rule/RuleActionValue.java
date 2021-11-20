package com.networknt.rule;

import java.util.Objects;

public class RuleActionValue {
    private String actionValueId;
    private String valueTypeCode;
    private String value;

    public RuleActionValue() {
    }

    public RuleActionValue(String actionValueId, String valueTypeCode, String value) {
        this.actionValueId = actionValueId;
        this.valueTypeCode = valueTypeCode;
        this.value = value;
    }

    public String getActionValueId() {
        return actionValueId;
    }

    public void setActionValueId(String actionValueId) {
        this.actionValueId = actionValueId;
    }

    public String getValueTypeCode() {
        return valueTypeCode;
    }

    public void setValueTypeCode(String valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleActionValue that = (RuleActionValue) o;
        return Objects.equals(actionValueId, that.actionValueId) && Objects.equals(valueTypeCode, that.valueTypeCode) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionValueId, valueTypeCode, value);
    }
}
