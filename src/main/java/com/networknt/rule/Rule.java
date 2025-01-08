package com.networknt.rule;

import java.util.Collection;
import java.util.Objects;

public class Rule {
    private String ruleId;
    private String ruleName;
    private String ruleVersion;
    private String hostId;        // rule is always associated with a host.
    private String ruleType;    // each host has a defined list of rule types from the rule-type ref table.
    private String common;  // public or private. only public rules can be shared or subscribed by other hosts.
    private String ruleGroup;     // a group of rules can be executed together for example a list of validation rules.
    private String ruleDesc;
    private String ruleOwner;
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

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public String getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(String ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getRuleOwner() {
        return ruleOwner;
    }

    public void setRuleOwner(String ruleOwner) {
        this.ruleOwner = ruleOwner;
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

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(ruleId, rule.ruleId) && Objects.equals(ruleName, rule.ruleName) && Objects.equals(ruleVersion, rule.ruleVersion) && Objects.equals(hostId, rule.hostId) && Objects.equals(ruleType, rule.ruleType) && Objects.equals(common, rule.common) && Objects.equals(ruleGroup, rule.ruleGroup) && Objects.equals(ruleDesc, rule.ruleDesc) && Objects.equals(ruleOwner, rule.ruleOwner) && Objects.equals(conditions, rule.conditions) && Objects.equals(conditionExpression, rule.conditionExpression) && Objects.equals(actions, rule.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, ruleName, ruleVersion, hostId, ruleType, common, ruleGroup, ruleDesc, ruleOwner, conditions, conditionExpression, actions);
    }
}
