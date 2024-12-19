# Rule Engine Debugging and Tracing

This document explains how to use the debugging and tracing features in the rule engine. These features are essential for understanding the evaluation process and troubleshooting complex rules.

## Why Debugging and Tracing?

As rules become more complex, it's important to have tools that allow you to see what's happening inside the rule engine during evaluation. Debugging and tracing provides:

*   **Transparency:** Observe how individual conditions, expressions, and custom operators are evaluated.
*   **Faster Debugging:** Quickly pinpoint the source of any unexpected behavior or errors.
*   **Improved Understanding:** Learn how your rules are processed by observing the evaluation flow.
*   **Complex Rule Troubleshooting:** Effectively troubleshoot complex rules involving nested expressions or custom operators.

## Enabling Tracing

The rule engine utilizes a separate logger for tracing called `rule.trace`. To enable tracing, you must configure your logging framework (e.g., Logback, Log4j2) to output `DEBUG` level logs for this logger.

### Logback Configuration Example

Add the following to your `logback.xml` (or equivalent logback configuration file):

```xml
<logger name="rule.trace" level="debug" additivity="false">
    <appender-ref ref="STDOUT"/>
</logger>

* This configuration sets the logging level to DEBUG for the rule.trace logger.

* additivity="false" prevents logs from being repeated due to parent loggers.

* <appender-ref ref="STDOUT"/> directs the logs to the console (or whatever appender is configured as STDOUT).

### Log4j2 Configuration Example

Add the following to your log4j2.xml (or equivalent Log4j2 configuration file):

```xml
<Logger name="rule.trace" level="debug" additivity="false">
    <AppenderRef ref="Console"/>
</Logger>
```

* This configuration sets the logging level to DEBUG for the rule.trace logger.

* additivity="false" prevents logs from being repeated due to parent loggers.

* <AppenderRef ref="Console"/> directs the logs to the console (or whatever appender is configured as Console).

After configuring your logging framework, trace messages will be output to the configured appender when the rules are executed.

## Tracing Output

When trace logging is enabled, the rule engine provides valuable output about the rule evaluation process. Here's a breakdown of the typical trace log output and what each part means:


## Condition Evaluation Tracing

This shows how each condition in a rule is being evaluated.

* Rule and Condition: Identifies the rule (ruleId) and the specific condition (conditionId).

```
DEBUG [main] rule.trace - Rule: test-rule, Condition: cid1
```

* Property Path and Objects: Shows the property path being accessed and the objects being compared.

  DEBUG [main] rule.trace - Rule: test-rule, Condition: cid1. PropertyPath: name, Object: test string, valueObject: test
  ```

*   **Operator Used:** Indicated the operator used during the evaluation
    ```
  DEBUG [main] rule.trace - Rule: test-rule, Condition: cid1. Operator: equals
  ```

*    **Custom Operator Used:** Indicated the custom operator name being used.
  ```
   DEBUG [main] rule.trace - Rule: test-rule, Condition: cid1. CustomOperator: containsIgnoreCase
   ```
*   **Result of Evaluation:** Shows if the condition evaluates to `true` or `false`.
  ```
   DEBUG [main] rule.trace - Rule: test-rule, Condition: cid1. Result: true
  ```

### Expression Evaluation Tracing

For rules using condition expressions, you will see a detailed trace of how the expression is being evaluated.

*   **Expression to be Evaluated:**
  ```
 DEBUG [main] rule.trace - Evaluating expression: (cid1 OR cid2) for rule: test-rule
  ```
*   **Tokens:** Shows each token in the expression.

DEBUG [main] rule.trace - Evaluating expression: (cid1 OR cid2), token: cid1


*   **Push and Pop of Parenthesis:** Shows when a parenthesis is pushed to the stack and when popped from the stack.

```
DEBUG [main] rule.trace - Pushing opening parenthesis to the stack: (, operator stack size: 1
DEBUG [main] rule.trace - Popping opening parenthesis from the stack, operator stack size: 0
```

Pushing and Popping operators Shows when an operator is pushed to the stack and which operator is being processed from the stack

```
DEBUG [main] rule.trace - Pushing operator to the stack: AND, operator stack size: 1
 DEBUG [main] rule.trace - Processing operator: AND, operator stack size: 1
```

Pushing condition results: Shows the result of condition being pushed to the stack.



```
DEBUG [main] rule.trace - Pushing condition result to the stack: true, for conditionId: cid1, stack size: 1
```

Result of operator: Shows the result of each logical operation.

```
DEBUG [main] rule.trace - Result of operator AND: true AND true is: true and push the result to the stack.
```

Final Result: Indicates the final result of the evaluated expression.

```
  DEBUG [main] rule.trace - Result for the expression: (cid1 OR cid2) is: true
```
##  Using the Trace Logs

1.  **Enable Debug Logging:** Make sure you have enabled the debug level logging for `rule.trace`.
2.  **Run your Rules:** When you execute a rule using your rule engine, the trace information will be output.
3.  **Analyze Output:** Carefully examine the trace output. Look for:
  *   Incorrect property path resolutions.
  *  Incorrect evaluation of conditions.
  *   Type conversion issues.
  *   Incorrect precedence of operators.
  *  Errors in custom operator implementation.

## Best Practices

*   **Enable Tracing Selectively:** Enable tracing only for specific rules that you want to debug. This minimizes overhead.
*   **Use a Separate Logger:** Using a separate logger (`rule.trace`) for the trace output makes it easier to enable/disable tracing without affecting other logs.
*   **Disable Tracing in Production:** Disable tracing in production because it can impact the performance of the rule engine.

## Summary

The rule engine's tracing capability provides valuable insights into how your rules are being evaluated. By using this feature, you can more effectively understand, debug, and troubleshoot even the most complex rules, leading to more reliable and maintainable rule systems. Remember to configure your logging correctly and selectively use tracing to avoid performance overhead.

