package com.networknt.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
        // Test input
        String ruleId = "123";
        String conditionId = "456";
        String errorMsg = "Test error occurred";

        try {
            // Simulate an exception
            throw new RuntimeException("Simulated exception for logging test");
        } catch (RuntimeException e) {
            // Log the exception with the stack trace
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
        }
    }
}
