package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class Rule {
    private String ruleId;
    private String host;        // rule is always associated with a host.
    private String ruleType;    // each host has a defined list of rule types from the rule-type ref table.
    private String visibility;  // public or private. only public rules can be shared or subscribed by other hosts.
    private String groupId;     // a group of rules can be executed together for example a list of validation rules.
    private String description;
    private Integer priority;
    private Collection<RuleCondition> conditions;
    private String conditionExpression;
    private Collection<RuleAction> actions;

    public Rule() {
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Collection<RuleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Collection<RuleCondition> conditions) {
        this.conditions = conditions;
    }
    
    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public Collection<RuleAction> getActions() {
        return actions;
    }

    public void setActions(Collection<RuleAction> actions) {
        this.actions = actions;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(ruleId, rule.ruleId) && Objects.equals(host, rule.host) && Objects.equals(ruleType, rule.ruleType) && Objects.equals(visibility, rule.visibility) && Objects.equals(groupId, rule.groupId) && Objects.equals(description, rule.description) && Objects.equals(priority, rule.priority) && Objects.equals(conditions, rule.conditions) && Objects.equals(conditionExpression, rule.conditionExpression) && Objects.equals(actions, rule.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, host, ruleType, visibility, groupId, description, priority, conditions, conditionExpression, actions);
    }
}
