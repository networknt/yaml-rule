# yaml-rule
A simple rule engine with rules defined in YAML or JSON



## Action Execution

#### Single Action

If the rule has only one action defined, it should be executed only if the rule evaluated to true. 

#### Multiple Actions

If the rule has multiple actions defined, iterate all actions to decide if the rule action should be executed or not. 

1. If the action conditionResult is true, only execute the action if the rule evaluation result is true. 
2. If the action conditionResult is false, only execute the action if the rule evaluation result is false. 
3. If there is no conditionResult defined for the action, execute the action regardless the evaluation result.


## getObjectByPath Cache 

* Cache Scope: The cache should be scoped to a single rule evaluation, meaning it should be cleared between rule evaluations. This will ensure that you don't have stale data from previous rule executions.

* Cache Key: The cache key should consist of both the propertyPath and the object instance itself so that different instances of the same class can be evaluated correctly. A composite key of property path, and the object id (using System.identityHashCode(object)) will be ideal.

* Cache Implementation: You can use a HashMap to implement the cache.

* Cache Clearing: The cache should be cleared in the evaluate method at the beginning of the method execution.

#### additonal tests

More Complex Scenarios: Add more test cases for different levels of object nesting, different access patterns, and edge cases to ensure comprehensive testing.

Logging: You might want to include a logging message in the getObjectByPath method when the cache is hit to help with debugging when troubleshooting.

Performance measurement: Run the test case with a large dataset and repeat it many times, measure the total execution time without cache and with cache to get a better idea of the performance improvement.

Concurrent testing: If your rule engine is running in a concurrent environment, create a multi threaded test case to check the thread safety of the cache.


## Rule Validation


* Problem: Some errors might be caught during rule evaluation when they could be caught much earlier during rule loading.

* Solution: Implement more comprehensive rule validation at the rule load/parse stage. For example:

  * Validate that referenced properties exist or are of the correct type.

  * Validate that operators and their value types are compatible (e.g., you can't use a > operator on a string without using LEN_GT, LEN_LT etc.).

  * Validate the structure and syntax of the conditionExpression.

  * Validate the existence of the action class.


* Problem: The current evaluateConditionExpression method doesn't handle parsing errors very well and throws a generic Exception in most of the parsing cases.

* Solution: Create a dedicated ExpressionParser class that is responsible for parsing the condition expression, and handle the errors such as:

  * Unbalanced parentheses

  * Invalid tokens

  * Missing operands for logical operators.

  * Invalid condition ids.

  * Throw ExpressionParseException with the location (position) of the parsing error, the expression and ruleId.

* Schema Validation: If you're loading rules from YAML/JSON, ensure you have a robust schema validation process to catch errors early.


## Pre-Processing

This is in the rule loader. Rule Pre-processing: If rules are static (don't change frequently), pre-process them during loading (e.g., parse and validate the conditionExpression, pre-compile regex patterns). This can speed up evaluation.





```
Performance:



Flexibility & Extensibility:


Variable Binding: Let actions access the object map or evaluation result (e.g., set a header based on a property in the object).

External Data Source: The rules can also involve external data sources, so the RuleEvaluator should be able to interact with them.

New Features I Would Suggest:

Rule Versioning:

Support storing different versions of rules and switching between them. This allows for safer changes to rules and the ability to rollback to previous versions.

Rule Conflict Detection:

Implement a mechanism to detect conflicts between rules based on priorities and conditions. This feature can be a valuable tool for the users to manage their rules more efficiently.

Rule Grouping/Categories:

Allow rules to be grouped into categories for better organization and management (e.g., validation, authorization, transformation).

Rule Performance Metrics:

Provide some basic metrics (e.g., average rule evaluation time, most frequently matched rules) to get insights on performance and identify bottlenecks.

Rule API:

Provide a REST API to manage rules, so rules can be managed dynamically from an admin console or external source without restarting the application.

Asynchronous Rule Evaluation:

Support asynchronous rule evaluation for scenarios where rule execution is time-consuming (e.g., when the action needs to communicate with external services) or in high-throughput environments.

Rule DSL (Domain Specific Language):

Explore a more user-friendly language for writing rules (instead of YAML). A DSL could provide a syntax that's easier to read and write, and can abstract away the underlying structure. You can build your own or use some available libraries.

How to Approach Improvements:

Prioritize: Don't try to do everything at once. Prioritize based on your project's needs. Start with critical issues (like error handling and performance bottlenecks) first.

Incremental Changes: Make small, testable changes. This reduces the risk of introducing regressions and makes debugging easier.

Testing: Add comprehensive unit tests for every new feature and for any changes to the existing code, especially for expression parsing and condition evaluation logic.

Refactoring: Refactor the code as you go along. Make sure that you remove code duplication, magic strings and improve the code readability.
```


### test cases

```
1. Condition Evaluation (evaluateCondition Method):

Basic Operators:

Test each operator (EQUALS, NOT_EQUALS, CONTAINS, NOT_CONTAINS, GREATER_THAN, LESS_THAN, etc.) with various data types (integers, strings, booleans, dates, nulls, etc.).

Test the operators with both String and non-String conditions.

Test with valid condition values and invalid condition values.

List Operators:

Test IN_LIST and NOT_IN_LIST with empty lists, single-element lists, and multi-element lists.

Test with different data types in the lists.

Test with lists containing null values and matching null values.

Null and Empty Operators:

Test IS_NULL, IS_NOT_NULL, IS_EMPTY, IS_NOT_EMPTY, IS_BLANK, IS_NOT_BLANK with nulls, empty strings, blank strings, and non-empty strings.

Date Operators:

Test BEFORE, AFTER, and ON with different date values and date formats.

Test with date values in the past, present, and future.

Test with valid and invalid date formats (with and without dateFormat specified).

Length Operators:

Test LENGTH_EQUALS, LENGTH_GREATER_THAN, and LENGTH_LESS_THAN with different string lengths and both integer and string values specified as the condition value, including negative numbers.

Regex Operators:

Test MATCH and NOT_MATCH with a variety of regular expressions including simple and complex regexes and different regex flags set.

Test with valid and invalid regex patterns.

Test with empty strings.

Test for different regex flags, with and without flags.

Type Conversion:

Test automatic type conversions with the optional valueTypeCode for different type combinations. Ensure that it throws error if it cannot convert and it compares correctly.

Test with different valueTypeCode including when it is not specified.

Error Cases:

Test with invalid operator codes, invalid or missing property paths, and invalid data types.

Test what happens when invalid valueTypeCode is provided.

Test with Null objects.

Edge Cases:

Test with very large and very small values, very long strings.

Test with complex object structures.

Test with different locales.

2. Expression Parsing (evaluateConditionExpression Method):

Basic Expressions:

Test with a single condition ID (cid1).

Test with a combination of two condition IDs (cid1 AND cid2, cid1 OR cid2).

Test with complex expressions including AND, OR and parenthesis.

Precedence:

Test with expressions containing AND and OR operators to ensure correct operator precedence. (e.g., cid1 OR cid2 AND cid3).

Parentheses:

Test with expressions containing nested parentheses. (e.g., (cid1 OR cid2) AND cid3, cid1 OR (cid2 AND (cid3 OR cid4))).

Missing Conditions:

Test with expressions containing condition IDs that are not present in the conditions collection to ensure that a RuleEngineException is thrown.

Invalid Expressions:

Test with malformed expressions (e.g., unbalanced parentheses, missing operands).

Empty Expressions:

Test with an empty expression or a expression with only spaces.

3. Object Path Resolution (getObjectByPath Method):

Basic Properties:

Test with a single-level property path (e.g., name).

Test with nested property paths (e.g., address.street, person.address.city).

Test with both valid and invalid property paths to ensure they return correct objects.

Map Access:

Test with property paths that access values within a Map (e.g., map.key, nestedMap.subMap.key).

List Access:

Test with property paths that access elements within a List using index (e.g., list[0], nestedList[2][1]).

Test with valid and out of bound index.

Method Access:

Test with property paths that access properties via getter methods (e.g., getName, getAddress().getCity()).

Null Handling:

Test when any of the object in the path is null.

Test with null input object and null propertyPath

Error Cases:

Test when an exception is thrown when calling the method using reflection.

Test when a property is not found, ensure the proper ConditionEvaluationException is thrown.

4. Type Conversion (convertConditionValue Method):

Test various types of conversion.

String to int

String to Long

String to Float

String to Double

String to BigDecimal

String to BigInteger

String to Byte

String to Boolean

String to Date

String to Sql Date

String to Sql Timestamp

String to String (No conversion)

Invalid Input:

Test conversion when a String cannot be converted into a specified type and an exception is thrown.

Test with different Date formats.

Test with null or empty String

5. Thread Safety

Test multiple threads are creating pattern at the same time with the same string and flags and make sure the pattern is created once.

6. General Test Cases

End to End:

Test different rules using a combination of different conditions and expression and see if they are working as expected.

Test complex scenarios by creating complex objects with a variety of properties.

Performance:

Test with a large number of conditions and make sure that performance is acceptable.

Recommendations:

Start Small: Begin by adding tests for the most common use cases and then gradually increase the coverage.

Use Test-Driven Development: Write your test before you write your code to make sure all code is testable and to have better code coverage.

Use Parametrized tests: Use parametrized tests to test the various scenarios and combinations.

Prioritize Regression Tests: Whenever you find a bug, create a unit test that reproduces the bug. This helps to prevent the same bug from reappearing after future changes.

Focus on Boundary values: Make sure you are including boundary values in your tests (0, null, "", very large and small values)
```
