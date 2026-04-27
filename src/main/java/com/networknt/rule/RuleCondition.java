package com.networknt.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class RuleCondition {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Collection<Object>> COLLECTION_TYPE = new TypeReference<>() {};

    private String conditionId;
    private String conditionDesc;
    private String propertyPath;
    private String operatorCode;
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

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getOperand() {
        return propertyPath;
    }

    public void setOperand(String operand) {
        this.propertyPath = operand;
    }

    public String getConditionDesc() {
        return conditionDesc;
    }

    public void setConditionDesc(String conditionDesc) {
        this.conditionDesc = conditionDesc;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperator() {
        return operatorCode;
    }

    public void setOperator(String operator) {
        this.operatorCode = RuleOperator.normalize(operator);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Collection<RuleConditionValue> getConditionValues() {
        return conditionValues;
    }

    public void setConditionValues(Collection<RuleConditionValue> conditionValues) {
        this.conditionValues = conditionValues;
    }

    public Object getExpected() {
        if (conditionValues == null || conditionValues.isEmpty()) {
            return null;
        }
        if (conditionValues.size() == 1) {
            return conditionValues.iterator().next().getConditionValue();
        }
        Collection<Object> expectedValues = new ArrayList<>();
        for (RuleConditionValue value : conditionValues) {
            expectedValues.add(value.getConditionValue());
        }
        return expectedValues;
    }

    public void setExpected(Object expected) {
        Collection<RuleConditionValue> values = new ArrayList<>();
        if (expected instanceof String) {
            String value = ((String) expected).trim();
            if (value.startsWith("[") && value.endsWith("]")) {
                try {
                    expected = OBJECT_MAPPER.readValue(value, COLLECTION_TYPE);
                } catch (Exception ignored) {
                    // Keep the original string if it is not valid JSON.
                }
            }
        }
        if (expected instanceof Collection<?>) {
            for (Object item : (Collection<?>) expected) {
                values.add(toConditionValue(item));
            }
        } else {
            values.add(toConditionValue(expected));
        }
        this.conditionValues = values;
    }

    private RuleConditionValue toConditionValue(Object expected) {
        RuleConditionValue value = new RuleConditionValue();
        value.setConditionValue(expected == null ? null : String.valueOf(expected));
        if (expected instanceof Number) {
            value.setValueTypeCode(expected instanceof Float || expected instanceof Double ? "DOUBLE" : "LONG");
        } else if (expected instanceof Boolean) {
            value.setValueTypeCode("BOOLEAN");
        } else {
            value.setValueTypeCode("STRING");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleCondition that = (RuleCondition) o;
        return Objects.equals(conditionId, that.conditionId) && Objects.equals(conditionDesc, that.conditionDesc) && Objects.equals(propertyPath, that.propertyPath) && Objects.equals(operatorCode, that.operatorCode) && Objects.equals(index, that.index) && Objects.equals(conditionValues, that.conditionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditionId, conditionDesc, propertyPath, operatorCode, index, conditionValues);
    }
}
