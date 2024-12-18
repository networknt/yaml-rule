package com.networknt.rule;

public enum LogicalOperator {
    AND("AND"),
    OR("OR");

    private final String operator;
    LogicalOperator(String operator) {
        this.operator = operator;
    }
    public String getOperator() {
        return this.operator;
    }

    public static LogicalOperator fromString(String text) {
        for (LogicalOperator op : LogicalOperator.values()) {
            if (op.operator.equalsIgnoreCase(text)) {
                return op;
            }
        }
        throw new IllegalArgumentException("No enum constant " + LogicalOperator.class.getCanonicalName() + "." + text);
    }
}

