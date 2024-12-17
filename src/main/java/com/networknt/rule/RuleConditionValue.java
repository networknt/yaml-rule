package com.networknt.rule;

import java.util.Objects;

public class RuleConditionValue {
    private String conditionValueId;
    private boolean expression;
    private String conditionValue;
    private String valueTypeCode;
    private String regexFlags;
    private String dateFormat;

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
        this.regexFlags = null;
        this.dateFormat = null;
    }

    public RuleConditionValue(String conditionValueId, boolean expression, String conditionValue, String valueTypeCode, String regexFlags) {
        this.conditionValueId = conditionValueId;
        this.expression = expression;
        this.conditionValue = conditionValue;
        this.valueTypeCode = valueTypeCode;
        this.regexFlags = regexFlags;
        this.dateFormat = null;
    }

    public RuleConditionValue(String conditionValueId, boolean expression, String conditionValue, String valueTypeCode, String regexFlags, String dateFormat) {
        this.conditionValueId = conditionValueId;
        this.expression = expression;
        this.conditionValue = conditionValue;
        this.valueTypeCode = valueTypeCode;
        this.regexFlags = regexFlags;
        this.dateFormat = dateFormat;
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

    public String getRegexFlags() {
        return regexFlags;
    }

    public void setRegexFlags(String regexFlags) {
        this.regexFlags = regexFlags;
        this.dateFormat = dateFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleConditionValue that = (RuleConditionValue) o;
        return expression == that.expression && Objects.equals(conditionValueId, that.conditionValueId) && Objects.equals(conditionValue, that.conditionValue) && Objects.equals(valueTypeCode, that.valueTypeCode) && Objects.equals(regexFlags, that.regexFlags) && Objects.equals(dateFormat, that.dateFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionValueId, expression, conditionValue, valueTypeCode, regexFlags, dateFormat);
    }
}
