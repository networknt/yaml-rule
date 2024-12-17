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






