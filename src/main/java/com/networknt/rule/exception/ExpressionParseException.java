package com.networknt.rule.exception;

public class ExpressionParseException extends RuleEngineException {
    private final String expression;
    private final int position;

    public ExpressionParseException(String message, String ruleId, String expression, int position) {
        super(message, ruleId);
        this.expression = expression;
        this.position = position;
    }
    public String getExpression() { return this.expression;}
    public int getPosition() { return this.position;}
}
