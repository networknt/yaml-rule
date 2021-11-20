package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class RuleCondition {
    private String conditionId;
    private String variableName;
    private String propertyPath;
    private String operatorCode;
    private String joinCode;
    private Integer index;
    private Collection<RuleConditionValue> conditionValues;

    public RuleCondition() {
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndexNum(Integer indexNum) {
        this.index = indexNum;
    }

    public Collection<RuleConditionValue> getConditionValues() {
        return conditionValues;
    }

    public void setConditionValues(Collection<RuleConditionValue> conditionValues) {
        this.conditionValues = conditionValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleCondition that = (RuleCondition) o;
        return Objects.equals(conditionId, that.conditionId) && Objects.equals(variableName, that.variableName) && Objects.equals(propertyPath, that.propertyPath) && Objects.equals(operatorCode, that.operatorCode) && Objects.equals(joinCode, that.joinCode) && Objects.equals(index, that.index) && Objects.equals(conditionValues, that.conditionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionId, variableName, propertyPath, operatorCode, joinCode, index, conditionValues);
    }
}
