package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class Rule {
    private String ruleId;
    private String host;
    private String serviceId;
    private String groupId;
    private String description;
    private Collection<RuleCondition> conditions;
    private Collection<RuleAction> actions;

    public Rule() {
    }

    public Rule(String ruleId, String host, String serviceId, String groupId, String description, Collection<RuleCondition> conditions, Collection<RuleAction> actions) {
        this.ruleId = ruleId;
        this.host = host;
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.description = description;
        this.conditions = conditions;
        this.actions = actions;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public void setRuleActions(Collection<RuleAction> actions) {
        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(ruleId, rule.ruleId) && Objects.equals(host, rule.host) && Objects.equals(serviceId, rule.serviceId) && Objects.equals(groupId, rule.groupId) && Objects.equals(description, rule.description) && Objects.equals(conditions, rule.conditions) && Objects.equals(conditions, rule.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, host, serviceId, groupId, description, conditions, actions);
    }
}
