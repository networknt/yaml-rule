package com.networknt.rule;

public enum RuleOperator {
    EQUALS("equals"),
    NOT_EQUALS("notEquals"),
    CONTAINS("contains"),
    NOT_CONTAINS("notContains"),
    IN_LIST("inList"),
    NOT_IN_LIST("notInList"),
    CONTAINS_ANY("containsAny"),
    CONTAINS_ALL("containsAll"),
    CONTAINS_NONE("containsNone"),
    GREATER_THAN("greaterThan"),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual"),
    LESS_THAN("lessThan"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),
    IS_NULL("isNull"),
    IS_NOT_NULL("isNotNull"),
    IS_EMPTY("isEmpty"),
    IS_NOT_EMPTY("isNotEmpty"),
    IS_BLANK("isBlank"),
    IS_NOT_BLANK("isNotBlank"),
    BEFORE("before"),
    AFTER("after"),
    ON("on"),
    LENGTH_EQUALS("lengthEquals"),
    LENGTH_GREATER_THAN("lengthGreaterThan"),
    LENGTH_LESS_THAN("lengthLessThan"),
    MATCH("match"),
    NOT_MATCH("notMatch");


    private final String operator;
    RuleOperator(String operator) {
        this.operator = operator;
    }
    public String getOperator() {
        return this.operator;
    }

    public static String normalize(String text) {
        if (text == null) return null;
        switch (text) {
            case "==":
            case "eq":
            case "EQ":
                return EQUALS.operator;
            case "!=":
            case "ne":
            case "NE":
                return NOT_EQUALS.operator;
            case ">":
                return GREATER_THAN.operator;
            case ">=":
                return GREATER_THAN_OR_EQUAL.operator;
            case "<":
                return LESS_THAN.operator;
            case "<=":
                return LESS_THAN_OR_EQUAL.operator;
            case "matches":
                return MATCH.operator;
            case "exists":
                return IS_NOT_NULL.operator;
            case "notExists":
                return IS_NULL.operator;
            default:
                return text;
        }
    }

    public static RuleOperator fromString(String text) {
        text = normalize(text);
        for (RuleOperator op : RuleOperator.values()) {
            if (op.operator.equalsIgnoreCase(text)) {
                return op;
            }
        }
        throw new IllegalArgumentException("No enum constant " + RuleOperator.class.getCanonicalName() + "." + text);
    }
}
