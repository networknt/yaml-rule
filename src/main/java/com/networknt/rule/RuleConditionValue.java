package com.networknt.rule;

import java.util.Objects;

public class RuleConditionValue {
    private String conditionValueId;
    private boolean expression;
    private String conditionValue;
    private String valueTypeCode;

    public RuleConditionValue() {
    }

    public RuleConditionValue(String conditionValueId, boolean expression, String conditionValue) {
        this.conditionValueId = conditionValueId;
        this.expression = expression;
        this.conditionValue = conditionValue;
        this.valueTypeCode = "STRING";
    }

    public RuleConditionValue(String conditionValueId, boolean expression, String conditionValue, String valueTypeCode) {
        this.conditionValueId = conditionValueId;
        this.expression = expression;
        this.conditionValue = conditionValue;
        this.valueTypeCode = valueTypeCode;
    }

    public String getConditionValueId() {
        return conditionValueId;
    }

    public void setConditionValueId(String conditionValueId) {
        this.conditionValueId = conditionValueId;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public boolean isExpression() {
        return expression;
    }

    public void setExpression(boolean expression) {
        this.expression = expression;
    }

    public String getValueTypeCode() {
        return valueTypeCode;
    }

    public void setValueTypeCode(String valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleConditionValue that = (RuleConditionValue) o;
        return expression == that.expression && Objects.equals(conditionValueId, that.conditionValueId) && Objects.equals(conditionValue, that.conditionValue) && Objects.equals(valueTypeCode, that.valueTypeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionValueId, expression, conditionValue, valueTypeCode);
    }
}
