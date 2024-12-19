# Custom Operators in the Rule Engine

This document explains how to define and use custom operators in the rule engine. Custom operators provide a way to extend the functionality of the rule engine by adding domain-specific logic for condition evaluation.

## Why Custom Operators?

Built-in operators, such as `equals`, `greaterThan`, or `contains`, are often sufficient for basic rule evaluation. However, in complex business domains, you might need more specialized logic. Custom operators provide:

*   **Domain-Specific Functionality:** Implement unique comparison or evaluation logic tailored to your specific requirements.
*   **Enhanced Expressiveness:** Create more readable rules, making it easier to understand the intent of your rules.
*   **Reduced Code Duplication:** Avoid repeating the same logic in different rules.
*   **Improved Maintainability:** Centralize and reuse domain-specific logic in custom operator classes.
*   **Flexibility:** Adapt to changing business requirements and use cases without changing the core rule engine.

## Defining Custom Operators

To create a custom operator, follow these steps:

1.  **Implement the `CustomOperator` Interface:**

    Create a class that implements the `com.networknt.rule.custom.CustomOperator` interface. This interface defines the contract for your custom operator.

    ```java
    package com.networknt.rule.custom;
import com.networknt.rule.exception.RuleEngineException;

import java.util.List;

public interface CustomOperator {
    String getOperatorName();
    boolean evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues) throws RuleEngineException;
}
    ```

    *   `getOperatorName()`: Returns the string name that will be used in the rules as the `operatorCode`.
    *   `evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues)`: Contains the core logic for evaluating the custom operator for given object against a given value.

2.  **Create a Concrete Operator Class:**
   Implement the `CustomOperator` in a concrete class. For example a `ContainsIgnoreCaseOperator`.
    ```java
    package com.networknt.rule.custom;

    import com.networknt.rule.exception.ConditionEvaluationException;
    import com.networknt.rule.exception.RuleEngineException;
    import com.networknt.rule.RuleConditionValue;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.util.List;

    public class ContainsIgnoreCaseOperator implements CustomOperator {
        private static final Logger logger = LoggerFactory.getLogger(ContainsIgnoreCaseOperator.class);
        @Override
        public String getOperatorName() {
            return "containsIgnoreCase";
        }

        @Override
        public boolean evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues) throws RuleEngineException {
            if (object == null || valueObject == null) {
                return (object == null && valueObject == null);
            }
             if (object instanceof String && valueObject instanceof String) {
                return ((String) object).toLowerCase().contains(((String) valueObject).toLowerCase());
            } else {
                String errorMsg = "Contains evaluation with type different than string " + object.getClass();
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
                return false;
            }
        }
    }

    ```
    Another example of `StartsWithOperator`
        ```java
    package com.networknt.rule.custom;

        import com.networknt.rule.exception.ConditionEvaluationException;
        import com.networknt.rule.exception.RuleEngineException;
        import com.networknt.rule.RuleConditionValue;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import java.util.List;

        public class StartsWithOperator implements CustomOperator {
            private static final Logger logger = LoggerFactory.getLogger(StartsWithOperator.class);
            @Override
            public String getOperatorName() {
                return "startsWith";
            }

            @Override
             public boolean evaluate(String ruleId, String conditionId, Object object, Object valueObject, List<RuleConditionValue> conditionValues) throws RuleEngineException {
                if (object == null || valueObject == null) {
                    return (object == null && valueObject == null);
                }
                if (object instanceof String && valueObject instanceof String) {
                    return ((String) object).startsWith((String) valueObject);
                } else {
                    String errorMsg = "StartsWith evaluation with type different than string " + object.getClass();
                    logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
                    return false;
                }
            }
        }
        ```
## Registering Custom Operators

To use a custom operator in your rules, you must register it with the `RuleEngine` using the `registerCustomOperator` method.

```java
    RuleEngine engine = new RuleEngine(ruleMap, null);
    CustomOperator customOperator = new ContainsIgnoreCaseOperator();
    engine.registerCustomOperator("containsIgnoreCase", customOperator);
    CustomOperator customOperator1 = new StartsWithOperator();
    engine.registerCustomOperator("startsWith", customOperator1);

```     

## Using Custom Operators in Rules

After registering the custom operator, you can use it in your rules with the operatorCode that matches the operator's name, just like built in operators.

Here is a sample rule

```
test-contains-ignore-case-rule:
  ruleId: test-contains-ignore-case-rule
  host: lightapi.net
  ruleType: generic
  visibility: public
  description: test rule with containsIgnoreCase custom operator
  priority: 1
  conditions:
    - conditionId: cid1
      propertyPath: name
      operatorCode: containsIgnoreCase
      index: 0
      conditionValues:
        - conditionValueId: cv1
          conditionValue: "test"
  actions:
    - actionId: act1
      actionClassName: com.networknt.rule.ValidationAction
```

In this rule, the containsIgnoreCase custom operator will be used when evaluating the condition on the object's name property.



