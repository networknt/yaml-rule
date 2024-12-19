let's delve into the concept of variable binding in the context of the rule engine's actions. This feature is all about enabling actions to dynamically access and utilize data from the object map (the input data) or the results of condition evaluations. It's a powerful mechanism that adds significant flexibility and utility to the actions.

## Why Variable Binding is Important

Without variable binding, your actions would be limited to performing predefined operations with fixed values. 

1.  **Access Input Data:** Retrieve specific properties from the input object map that is passed into the rule engine at runtime, making the actions dynamic.

2.  **Use Evaluation Results:** Access the result of a specific rule condition that was previously evaluated as true.

3.  **Dynamic Behavior:** Set headers, message content, or other action parameters dynamically, which depends on the input data or previous evaluation results.

4.  **Complex Data Manipulation:** Transform or manipulate input data based on the properties being evaluated.

5.  **Context-Aware Actions:** Execute actions that are specific to the context of the rule, by accessing information from the current context.

**How Variable Binding Works**

Here's a typical approach to implementing variable binding in your rule engine:

1.  **Syntax for Variables:**
    *   Define a syntax for referencing variables within the action configuration. Common syntaxes include:
        *   `${propertyName}`: For referencing object map properties.
        *   `#{conditionId}` : For referencing results of conditions
        *   `${propertyName.nestedProperty}`: For nested properties within an object map.
    *  This syntax allows the action to easily find variables during action execution.

2.  **Variable Resolver:**

    *   Create a component (or method) that is responsible for resolving variables.
    *   This component should:
        *   Parse a variable expression (e.g., `${propertyName}`).
        *   Lookup the corresponding value either in the `objMap`, or in the `resultMap`.
        *    Handle null or not-found cases.
        *   Handle nested properties.

        ```java
       public Object resolveVariable(String variable, Map objMap, Map resultMap) {
            if (variable == null || variable.isEmpty()) {
                return variable;
           }
             if(variable.startsWith("${") && variable.endsWith("}")) {
                  String propertyPath = variable.substring(2, variable.length() - 1);
                   try {
                      Object obj = getObjectByPath(null, null, propertyPath, objMap);
                        return obj;
                   } catch(Exception e) {
                       // if object does not exist in objMap, return null
                       return null;
                  }

           } else  if(variable.startsWith("^{") && variable.endsWith("}")) {
              String conditionId = variable.substring(2, variable.length() - 1);
               if(resultMap != null && resultMap.containsKey(conditionId)) {
                  return resultMap.get(conditionId);
               }
               return null;
             }
            return variable;
       }
        ```

3.  **Action Configuration:**

    *   Your action configuration should allow you to specify the actions parameters using the defined variable syntax.

    *   For example, you may have a `setHeader` action with the following configuration in your YAML file:

        ```yaml
            actions:
                - actionId: act1
                    actionClassName: com.networknt.rule.SetHeaderAction
                    parameters:
                       headerName: X-Custom-Header
                       headerValue: "${name}"
        ```

4.  **Action Execution:**

    *   During action execution, use the variable resolver to resolve all variables in the action configuration.
    *    For example the `SetHeaderAction` can resolve the header value before setting the headers to the request.

        ```java
         public class SetHeaderAction implements RuleAction {
            private static final Logger logger = LoggerFactory.getLogger(SetHeaderAction.class);
           @Override
             public void performAction(String ruleId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
                 String headerName = (String) parameters.get("headerName");
                 String headerValue = (String) RuleEvaluator.getInstance().resolveVariable((String) parameters.get("headerValue"), objMap, resultMap);
                  if(headerName != null && headerValue != null) {
                      // set header using headerName and headerValue
                      logger.info("setting header " + headerName + " with value " + headerValue);
                   }
             }
         }
        ```
        This class gets the `headerName` as is and resolves the `headerValue` and sets header if both are valid values.

**Example Scenario**

Imagine your rule engine is used to process HTTP requests. You might have a rule to check for a specific customer ID in a request header. If the rule matches, an action might add some more headers to the request:

```yaml
my-custom-rule:
  ruleId: my-custom-rule
  conditions:
    - conditionId: cid1
      propertyPath: headers.customer-id
      operatorCode: equals
      conditionValues:
        - conditionValue: "12345"
  actions:
    - actionId: setHeader1
      actionClassName: com.networknt.rule.SetHeaderAction
      parameters:
        headerName: X-Special-Customer
        headerValue: "true"
    - actionId: setHeader2
      actionClassName: com.networknt.rule.SetHeaderAction
      parameters:
         headerName: X-Customer-Name
         headerValue: "${name}"
    - actionId: setHeader3
      actionClassName: com.networknt.rule.SetHeaderAction
       parameters:
          headerName: X-Rule-Result
          headerValue: "^{cid1}"


```

In this example:

*   The first action sets a header to a fixed value.
*   The second action uses variable binding to set the `X-Customer-Name` header to the value of the `name` property from the input object map.
* The third action uses the variable binding to set header to the evaluation result of `cid1`.

**Benefits of Variable Binding:**

*   **Dynamic Rules:** Allows you to create rules that respond to different input data.
*   **Reusability:** You can reuse actions for different rules by varying the parameters through variable binding.
*   **Less Code:** You don't need to write custom actions for every scenario. You can use variable binding to make them more generic.

**Recommendations:**

*   **Keep Syntax Simple:** Use a syntax for variables that is easy to understand and use.
*  **Handle Nested Objects:** Implement support for nested properties so that you are able to access properties from deep within the object.
*  **Graceful Handling of Missing Variables:** Ensure that your resolver handles missing properties or unresolved variables gracefully.
*  **Use caching:** You can use caching to make sure the calls to getObjectByPath are not made repeatedly.

Variable binding is a powerful feature that increases the flexibility and usability of your rule engine. You can make the actions more robust by supporting variable binding. Let me know if you have any further questions.


## It is necesary

If actions already have access to `objMap`, `resultMap`, and the entire `RuleAction` object, why bother with variable binding? It seems like a more complex approach when the data is already available directly.

Let's break down the reasons why variable binding, even with full access to those objects, is still a valuable pattern:

**1. Abstraction and Encapsulation:**

*   **Simplified Action Logic:** Variable binding allows actions to focus on their core purpose (e.g., setting a header, sending a message, performing a transformation) without needing to know the specifics of how to navigate the object map or resultMap to find a specific value. It abstracts the data retrieval logic and lets the actions use simplified parameters.
*   **Reduced Complexity:** Actions become simpler because they don't need to contain logic for accessing properties from maps or conditional checks on whether a result is present or not.
*   **Improved Readability:** By using variable placeholders, the intent of the action is clearer. For example, instead of seeing something like `objMap.get("address").get("city")` you see `${address.city}`. This is much easier to read.
*   **Isolation:** Actions are isolated from changes in the structure of `objMap` or `resultMap` as long as the variable name and logic is same.

**2. Control and Configuration:**

*   **Declarative Configuration:** Variable binding moves the data access logic from Java code into the rule configuration (e.g., YAML files). This makes it more declarative, where the logic of the action is defined more by the configuration and less by the code itself.
*   **Flexibility Through Configuration:** It empowers users to customize data usage through configuration, which allows more flexibility without requiring code changes. This is very useful in a system that is intended to be used by users who do not have a lot of coding experience.
*   **Consistency:** Enforces a consistent way of accessing values across different actions.
*   **Centralized Logic:** Centralizes the data resolution logic within the variable resolver rather than scattering it across multiple actions.

**3. Security and Control:**

*   **Restricted Access:** By providing a variable resolver, you have more control over which parts of the object map and resultMap are accessible to actions. You can apply custom logic to resolve specific types of variables or restrict access to certain values.
*   **Prevent Unauthorized Access:** You can prevent actions from accessing the entire object map and result map.

**4. Separation of Concerns:**

*   **Action Focus:** Actions are responsible for *what* they do (e.g., set a header) and not *how* they get the data. Variable binding ensures the actions are not responsible for how to retrieve data from the maps.
*   **Resolution Responsibility:** The variable resolver takes care of *how* data is retrieved from maps.
*   **Improved Maintainability:** This separation of concerns makes the code more maintainable as the responsibility of data retrieval is now centralized.

**5. Future Flexibility:**

*   **Potential for More Complex Resolvers:** A well-defined variable resolver is a starting point to support more sophisticated use cases in the future, such as functions, complex data transformation, etc.
*    **Support for new sources:** You can add support for resolving data from other sources, such as external APIs, by only changing the variable resolver.

**Analogy:**

Think of variable binding like using SQL parameters in a database query. You could technically build a query by manually concatenating strings, but parameters offer better security, readability, and abstraction.

**When Direct Access Might Be Acceptable:**

There are situations where direct access might be more appropriate, such as:

*   **Simple Actions:** If an action is very simple and directly works with the data from the object map without any extra transformation, it might be fine to access the data directly.
*   **Internal Actions:** For actions that are tightly integrated with the rule engine and do not need any external configuration, you can decide to use the direct access.

**In Summary:**

Variable binding is essential because:

*   It provides **abstraction and encapsulation** that make actions easier to manage and understand.
*   It offers **control and flexibility** in how actions are configured and used.
*   It enforces **separation of concerns** and promotes code maintainability.

While actions technically *can* access the object map and resultMap directly, variable binding promotes a more maintainable, flexible, and robust architecture in the long run. It ensures that each component has clear responsibilities, and users can customize the behavior through configuration instead of writing the logic in the code directly.


## Implementation


```markdown
# Updated Rule Action Configuration with Parameters and Variable Binding

This document explains the changes to the `RuleAction` class and the `IAction` interface to support action parameters and variable binding.

## RuleAction Updates

The `RuleAction` class has been updated to include a `parameters` field which is a map of key/value pairs.

```java
public class RuleAction {
    private String actionId;
    private String actionDesc;
    private String actionClassName;
    private Boolean conditionResult;
    private Collection<RuleActionValue> actionValues;
    private Map<String, Object> parameters;

    // Constructors, getters, setters and other methods
}
```

*   `parameters`: A `Map<String, Object>` that stores parameters for the action. This is populated when the rule is loaded. The `performAction` method in the IAction interface will have access to this. The keys of this map can be any string and the values are the resolved values based on the variable binding.

## IAction Interface Updates

The `IAction` interface has been modified to include the `actionId` and the `parameters` Map in both `performAction` and `postPerformAction`.

```java
public interface IAction {
    /**
     * Action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param parameters configuration parameters including the result from the variable binding.
     * @throws RuleEngineException exception thrown during performAction execution.
     */
    void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException;

    /**
     * post action to be performed
     * @param ruleId rule id
     * @param actionId action id
     * @param objMap input object
     * @param resultMap rule evaluation result map.
     * @param parameters configuration parameters including the result from the variable binding.
      * @throws RuleEngineException exception thrown during postPerformAction execution.
     */
    default void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
        // NOOP for classes implement IAction.
    };
}
```

*   `performAction`: This method now takes `ruleId`, `actionId`, `objMap`, `resultMap`, and `parameters` as arguments.
    * `ruleId`: Id of the rule that has matched.
    * `actionId`: Id of the action that is being executed.
    *  `objMap`: Original object map input into the rule engine.
    *  `resultMap`: A map containing the results of condition evaluations during rule execution.
    * `parameters`: Map of resolved parameters for the actions. The parameters are key-value pairs defined in the rule action definition and the values are resolved using variable binding logic.

*   `postPerformAction`: This is added for extensibility purposes, with the same arguments as `performAction`. This method has default implementation as `NOOP` and can be overridden for doing some processing after the action is executed.

## Purpose of the Changes

1.  **Explicit Action Parameters:** The new parameters field in `RuleAction` provides a way to pass parameters to `IAction` implementations in a structured way.

2.  **Variable Binding:** The `parameters` field in `IAction` implementation is populated after resolving any variables. This empowers actions to use both fixed values and dynamically resolved values based on the input object map or the result of condition evaluations.

3.  **Action Context:** The addition of `ruleId` and `actionId` allows the action class to determine the context from which it is being called.

4.  **Extensibility**: `postPerformAction` provides ability to add extensibility for post processing the action.

## Using the Changes

1.  **Define Actions with Parameters in Rules:** In your rule definitions (e.g., YAML), you can specify parameters for an action under the `parameters` key within the action definitions.

    Example YAML configuration:

    ```yaml
    actions:
        - actionId: setHeader1
            actionClassName: com.networknt.rule.TestSetHeaderAction
            parameters:
                headerName: X-Custom-Header
                headerValue: "${name}"
    ```

2.  **Implement `IAction`:** Implement the `IAction` interface and receive the `ruleId`, `actionId`, `objMap`, `resultMap`, and `parameters` as method arguments and perform the action with these resolved parameters.

    Example IAction implementation:

    ```java
    public class TestSetHeaderAction implements IAction {
        private static final Logger logger = LoggerFactory.getLogger(TestSetHeaderAction.class);
        @Override
         public void performAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {
             String headerName = (String) parameters.get("headerName");
              Object headerValue =  parameters.get("headerValue");

             if(headerName != null && headerValue != null) {
                 // set header using headerName and headerValue
                 logger.info("setting header " + headerName + " with value " + headerValue);
                  resultMap.put(headerName, headerValue);
              }
        }
         @Override
         public void postPerformAction(String ruleId, String actionId, Map<String, Object> objMap, Map<String, Object> resultMap, Map<String, Object> parameters) throws RuleEngineException {

        }
    }
    ```

3.  **Access Parameters in Actions:** Access parameters within your action logic using the `parameters` Map.

## Variable Binding

The values in the `parameters` Map can include placeholders that are resolved at runtime:

*   `${propertyName}`: References a property from the input `objMap`.
*   `^{conditionId}`: References the result of a condition evaluation in `resultMap`.
*   If the variable does not match any of the above placeholder patterns, it is returned as is.

The `RuleEvaluator` will resolve these placeholders to real values before calling the `performAction` method.

## Benefits of These Changes

*   **More Configurable Actions:** The changes provide a more flexible way to configure actions, reducing the need for custom action classes for simple tasks.
*   **Dynamic Actions:** Actions can now dynamically set parameters based on the input object map or the results of condition evaluations.
*  **Contextual Information:** The actions now have access to the rule id and action id.
*   **Enhanced Testability:** Actions can be more easily unit tested with the parameters, instead of relying on access to the full objectMap or resultMap.
*  **Extensibility:** The `postPerformAction` provides a way to extend the functionality by performing some processing after the action is executed.

By adopting these changes, your rule engine will be much more powerful and adaptable for a variety of use cases. Let me know if you have any questions.


