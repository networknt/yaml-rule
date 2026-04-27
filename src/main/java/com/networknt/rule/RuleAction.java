package com.networknt.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class RuleAction {
    private String actionId;
    private String actionDesc;
    private String actionRef;
    private Boolean conditionResult;
    private Collection<RuleActionValue> actionValues;
    private Map<String, Object> actionValueMap;
    private Map<String, Object> parameters;

    public RuleAction() {
    }

    public RuleAction(String actionId, String actionDesc, String actionRef, Boolean conditionResult, Collection<RuleActionValue> actionValues, Map<String, Object> parameters) {
        this.actionId = actionId;
        this.actionDesc = actionDesc;
        this.actionRef = actionRef;
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

    public String getActionRef() {
        return actionRef;
    }

    public void setActionRef(String actionRef) {
        this.actionRef = actionRef;
    }

    @JsonSetter("actionClassName")
    public void setActionClassName(String actionClassName) {
        this.actionRef = actionClassName;
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

    @JsonSetter("actionValues")
    public void setActionValueObject(Object actionValues) {
        if (actionValues instanceof Map) {
            Map<String, Object> values = (Map<String, Object>) actionValues;
            this.actionValueMap = values;
            this.actionValues = toActionValueCollection(values);
        } else if (actionValues instanceof Collection) {
            this.actionValueMap = null;
            this.actionValues = toActionValueCollection((Collection<?>) actionValues);
        } else {
            this.actionValueMap = null;
            this.actionValues = null;
        }
    }

    @JsonIgnore
    public Map<String, Object> getActionValueMap() {
        return actionValueMap;
    }

    public static Collection<RuleActionValue> toActionValueCollection(Map<String, Object> actionValues) {
        if (actionValues == null) {
            return null;
        }
        Collection<RuleActionValue> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : actionValues.entrySet()) {
            RuleActionValue value = new RuleActionValue();
            value.setActionValueId(entry.getKey());
            Object rawValue = entry.getValue();
            value.setValue(rawValue == null ? null : String.valueOf(rawValue));
            value.setValueTypeCode(valueTypeCode(rawValue));
            values.add(value);
        }
        return values;
    }

    public static Collection<RuleActionValue> toActionValueCollection(Collection<?> actionValues) {
        if (actionValues == null) {
            return null;
        }
        Collection<RuleActionValue> values = new ArrayList<>();
        for (Object item : actionValues) {
            if (item instanceof RuleActionValue) {
                values.add((RuleActionValue)item);
            } else if (item instanceof Map) {
                Map<?, ?> map = (Map<?, ?>)item;
                RuleActionValue value = new RuleActionValue();
                Object actionValueId = map.get("actionValueId");
                Object rawValue = map.containsKey("value") ? map.get("value") : map.get("conditionValue");
                Object valueTypeCode = map.get("valueTypeCode");
                value.setActionValueId(actionValueId == null ? null : String.valueOf(actionValueId));
                value.setValue(rawValue == null ? null : String.valueOf(rawValue));
                value.setValueTypeCode(valueTypeCode == null ? valueTypeCode(rawValue) : String.valueOf(valueTypeCode));
                values.add(value);
            }
        }
        return values;
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
        return Objects.equals(actionId, that.actionId) && Objects.equals(actionDesc, that.actionDesc) && Objects.equals(actionRef, that.actionRef) && Objects.equals(conditionResult, that.conditionResult) && Objects.equals(actionValues, that.actionValues) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, actionDesc, actionRef, conditionResult, actionValues, parameters);
    }

    private static String valueTypeCode(Object value) {
        if (value instanceof Float || value instanceof Double) {
            return "DOUBLE";
        }
        if (value instanceof Number) {
            return "LONG";
        }
        if (value instanceof Boolean) {
            return "BOOLEAN";
        }
        return "STRING";
    }
}
