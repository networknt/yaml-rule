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
    private int priority;
    private Collection<RuleCondition> conditions;
    private Collection<RuleAction> actions;

    public Rule() {
    }

    public Rule(String ruleId, String host, String ruleType, String visibility, String groupId, String description, Collection<RuleCondition> conditions, Collection<RuleAction> actions) {
        this.ruleId = ruleId;
        this.host = host;
        this.ruleType = ruleType;
        this.visibility = visibility;
        this.groupId = groupId;
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
        this.priority = 0;
    }
    public Rule(String ruleId, String host, String ruleType, String visibility, String groupId, String description, Collection<RuleCondition> conditions, Collection<RuleAction> actions, int priority) {
        this.ruleId = ruleId;
        this.host = host;
        this.ruleType = ruleType;
        this.visibility = visibility;
        this.groupId = groupId;
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
        this.priority = priority;
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

    public Collection<RuleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Collection<RuleCondition> conditions) {
        this.conditions = conditions;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(ruleId, rule.ruleId) && Objects.equals(host, rule.host) && Objects.equals(ruleType, rule.ruleType) && Objects.equals(visibility, rule.visibility) && Objects.equals(groupId, rule.groupId) && Objects.equals(description, rule.description) && Objects.equals(conditions, rule.conditions) && Objects.equals(actions, rule.actions) && Objects.equals(priority, rule.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, host, ruleType, visibility, groupId, description, conditions, actions, priority);
    }
}
