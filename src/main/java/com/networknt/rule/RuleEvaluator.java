package com.networknt.rule;

import com.networknt.rule.custom.ContainsIgnoreCaseOperator;
import com.networknt.rule.custom.CustomOperator;
import com.networknt.rule.custom.StartsWithOperator;
import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.InvalidOperatorException;
import com.networknt.rule.exception.RuleEngineException;
import com.networknt.rule.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RuleEvaluator {
    private static RuleEvaluator instance = null;

    /**
     * Logger instance for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(RuleEvaluator.class);
    private static final Logger traceLogger = LoggerFactory.getLogger("rule.trace");

    private static final Map accessorMap = new HashMap();
    private static final Class[] EMPTY_CLASS_LIST = new Class[0];


    private static HashSet simpleTypes;


    private static final ThreadLocal<Map<String, Object>> objectCache = ThreadLocal.withInitial(HashMap::new);
    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    private final Map<Class<?>, TypeSpecificOperation<?>> typeStrategies = new HashMap<>();

    protected static final Map<String, CustomOperator> customOperatorRegistry = new HashMap<>();


    static {
        simpleTypes = new HashSet();
        simpleTypes.add("java.lang.String");
        simpleTypes.add("java.lang.Integer");
        simpleTypes.add("java.math.BigDecimal");
        simpleTypes.add("java.math.BigInteger");
        simpleTypes.add("java.lang.Long");
        simpleTypes.add("java.lang.Boolean");
        simpleTypes.add("java.lang.Byte");
        simpleTypes.add("java.lang.Character");
        simpleTypes.add("java.lang.Double");
        simpleTypes.add("java.lang.Float");
        simpleTypes.add("java.lang.Short");
        simpleTypes.add("java.sql.Timestamp");
    }

    private RuleEvaluator() {
        typeStrategies.put(Integer.class, new IntegerTypeOperation());
        typeStrategies.put(BigDecimal.class, new BigDecimalTypeOperation());
        typeStrategies.put(Long.class, new LongTypeOperation());
        typeStrategies.put(BigInteger.class, new BigIntegerTypeOperation());
        typeStrategies.put(Byte.class, new ByteTypeOperation());
        typeStrategies.put(Double.class, new DoubleTypeOperation());
        typeStrategies.put(Float.class, new FloatTypeOperation());
        typeStrategies.put(Short.class, new ShortTypeOperation());
        typeStrategies.put(String.class, new StringTypeOperation());
        typeStrategies.put(Date.class, new DateTypeOperation());
        typeStrategies.put(java.sql.Date.class, new DateTypeOperation());
        typeStrategies.put(Timestamp.class, new DateTypeOperation());
        customOperatorRegistry.put("containsIgnoreCase", new ContainsIgnoreCaseOperator());
        customOperatorRegistry.put("startsWith", new StartsWithOperator());

    }

    public static synchronized RuleEvaluator getInstance() {
        if (instance == null) {
            instance = new RuleEvaluator();
        }
        return instance;
    }



    /**
     * Evaluate a map of objects based on the rule
     *
     * @param rule   rule
     * @param objMap input map
     * @return true if the rule matches the orphan, false otherwise.
     * @throws RuleEngineException if there is an error evaluating the rule
     */
    public boolean evaluate(Rule rule, Map objMap, Map resultMap) throws RuleEngineException {
        objectCache.get().clear();
        boolean result;
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) {
            return true;
        }

        if (rule.getConditionExpression() != null && !rule.getConditionExpression().trim().isEmpty()) {
            result = evaluateConditionExpression(rule.getRuleId(), rule.getConditionExpression(), rule.getConditions(), objMap, resultMap);
        } else {
            // Default to AND all conditions if no expression is given for backward compatibility.
            result = true;
            for (RuleCondition rc : rule.getConditions()) {
                boolean r = evaluateCondition(rule.getRuleId(), rc.getConditionId(), rc.getPropertyPath(), rc.getOperatorCode(),
                        (List) rc.getConditionValues(), objMap);
                resultMap.put(rc.getConditionId(), r);
                if (!r) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }


    /**
     * Evaluate a logical expression with condition ids
     */
    boolean evaluateConditionExpression(String ruleId, String expression, Collection<RuleCondition> conditions, Map objMap, Map resultMap) throws RuleEngineException {
        // A simple stack-based parser for expressions like "(cid1 OR (cid2 AND cid3))"
         traceLogger.debug("Evaluating expression: {} for rule: {}", expression, ruleId);
        Stack<Boolean> stack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "() ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue; // Skip empty tokens
                traceLogger.debug("Evaluating expression: {}, token: {}", expression, token);
            if (token.equals(RuleConstants.LEFT_PARENTHESIS)) {
                 operatorStack.push(token);
                traceLogger.debug("Pushing opening parenthesis to the stack: {}, operator stack size: {}", token, operatorStack.size());
            } else if (token.equals(RuleConstants.RIGHT_PARENTHESIS)) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals(RuleConstants.LEFT_PARENTHESIS)) {
                    processOperator(ruleId, stack, operatorStack, objMap, resultMap, conditions);
                 }
               if (!operatorStack.isEmpty() && operatorStack.peek().equals(RuleConstants.LEFT_PARENTHESIS)) {
                     operatorStack.pop();
                      traceLogger.debug("Popping opening parenthesis from the stack, operator stack size: {}", operatorStack.size());
                }
             } else {
                LogicalOperator logicalOperator = null;
                try {
                    logicalOperator =  LogicalOperator.fromString(token);
                } catch (IllegalArgumentException e) {
                    // Token is not a logical operator, assume it's a condition ID
                }
                if (logicalOperator == LogicalOperator.AND || logicalOperator ==  LogicalOperator.OR) {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals(RuleConstants.LEFT_PARENTHESIS) && hasHigherPrecedence(operatorStack.peek(), token)) {
                         processOperator(ruleId, stack, operatorStack, objMap, resultMap, conditions);
                   }
                     operatorStack.push(token);
                      traceLogger.debug("Pushing operator to the stack: {}, operator stack size: {}", token, operatorStack.size());
                } else {
                    // Assume this is a condition id
                    boolean conditionResult;
                    if (resultMap.containsKey(token)) {
                        conditionResult = (Boolean) resultMap.get(token);
                    } else {
                        RuleCondition ruleCondition = getRuleCondition(token, conditions);
                        if (ruleCondition == null) {
                            String errorMsg = "Condition with id: " + token + " is not defined";
                            logger.error("Error evaluating condition in rule {} missing condition {}: {}", ruleId, token, errorMsg);
                            throw new RuleEngineException(errorMsg, ruleId);
                        }
                        conditionResult = evaluateCondition(ruleId, ruleCondition.getConditionId(), ruleCondition.getPropertyPath(), ruleCondition.getOperatorCode(),
                                (List) ruleCondition.getConditionValues(), objMap);
                         resultMap.put(token, conditionResult);
                     }
                     traceLogger.debug("Pushing condition result to the stack: {}, for conditionId: {}, stack size: {}", conditionResult, token, stack.size() + 1);
                     stack.push(conditionResult);
                }
            }
        }
        while (!operatorStack.isEmpty()) {
            processOperator(ruleId, stack, operatorStack, objMap, resultMap, conditions);
        }
        if (stack.size() != 1) {
            String errorMsg = "Invalid expression " + expression + ". Stack size " + stack + " 1";
            logger.error("Error evaluating condition in rule {}, expression {}: {}", ruleId, expression, errorMsg);
            throw new RuleEngineException(errorMsg, ruleId);
        }
       boolean result = stack.pop();
        traceLogger.debug("Result for the expression: {} is: {}", expression, result);
         return result;
    }


    private boolean hasHigherPrecedence(String op1, String op2) {
        if (op1.equals(RuleConstants.LEFT_PARENTHESIS) || op1.equals(RuleConstants.RIGHT_PARENTHESIS)) {
            return false;
        }
        if (op2.equals(RuleConstants.LEFT_PARENTHESIS) || op2.equals(RuleConstants.RIGHT_PARENTHESIS)) {
            return true;
        }
        return LogicalOperator.fromString(op1) == LogicalOperator.AND && LogicalOperator.fromString(op2) == LogicalOperator.OR;
    }

    private void processOperator(String ruleId, Stack<Boolean> stack, Stack<String> operatorStack, Map objMap, Map resultMap, Collection<RuleCondition> conditions) throws RuleEngineException {
        String operator = operatorStack.pop();
        traceLogger.debug("Processing operator: {}, operator stack size: {}", operator, operatorStack.size());

        if (LogicalOperator.fromString(operator) == LogicalOperator.AND || LogicalOperator.fromString(operator) == LogicalOperator.OR) {
            if (stack.size() < 2) {
                String errorMsg = "Invalid expression. Stack size " + stack.size() + " is less than 2 for an operator " + operator;
                logger.error("Error evaluating condition in rule {}: {}", ruleId, errorMsg);
                throw new RuleEngineException(errorMsg, ruleId);
            }
            boolean operand2 = stack.pop();
            boolean operand1 = stack.pop();
             traceLogger.debug("popped two operands: {} and {} from the stack. stack size: {}. ", operand1, operand2, stack.size());
              boolean result = false;
            if (LogicalOperator.fromString(operator) == LogicalOperator.AND) {
               result = operand1 && operand2;
            } else if (LogicalOperator.fromString(operator) == LogicalOperator.OR) {
               result = operand1 || operand2;
             }
           traceLogger.debug("Result of operator {}: {} is: {} and push the result to the stack.", operator, operand1 + " " + operator + " " + operand2, result);
           stack.push(result);
         }
    }

    private RuleCondition getRuleCondition(String conditionId, Collection<RuleCondition> conditions) {
        for (RuleCondition condition : conditions) {
            if (condition.getConditionId().equals(conditionId)) {
                return condition;
            }
        }
        return null;
    }


    /**
     * Evaluates values passed as strings based on their type and the operator.
     */
    protected boolean evaluateCondition(String ruleId, String conditionId, String propertyPath,
                                        String opCode, List<RuleConditionValue> conditionValues, Object object) throws RuleEngineException {
        if (object == null) {
            String errorMsg = "Input object is null for operator " + opCode;
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        Object valueObject = null;
        RuleConditionValue conditionValue = null;
        if (conditionValues != null && !conditionValues.isEmpty()) {
            conditionValue = (RuleConditionValue) conditionValues.get(0);
            if (conditionValue != null) {
                if (conditionValue.isExpression()) {
                    valueObject = getObjectByPath(ruleId, conditionId, conditionValue.getConditionValue(), object);
                } else {
                    valueObject = conditionValue.getConditionValue();
                }
            }
        }
        Object resolvedObject = getObjectByPath(ruleId, conditionId, propertyPath, object);
        traceLogger.debug("Rule: {}, Condition: {}. PropertyPath: {}, Object: {}, valueObject: {}", ruleId, conditionId, propertyPath, resolvedObject, valueObject);

        RuleOperator operator = null;
        try {
            operator = RuleOperator.fromString(opCode);
        } catch (IllegalArgumentException e) {
            // if the operator is not one of the built-in operators, check if we have a custom operator
        }
        boolean result = false;
        if (operator != null) {
            switch (operator) {
                case EQUALS:
                    return evaluateEquals(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case NOT_EQUALS:
                    return !evaluateEquals(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case CONTAINS:
                    return evaluateContains(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case NOT_CONTAINS:
                    return !evaluateContains(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case IN_LIST:
                    return evaluateInList(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), conditionValues);
                case NOT_IN_LIST:
                    return !evaluateInList(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), conditionValues);
                case GREATER_THAN:
                    return evaluateGreaterThan(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case GREATER_THAN_OR_EQUAL:
                    return evaluateGreaterThanOrEqual(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case LESS_THAN:
                    return evaluateLessThan(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case LESS_THAN_OR_EQUAL:
                    return evaluateLessThanOrEqual(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
                case IS_NULL:
                    return isNull(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case IS_NOT_NULL:
                    return !isNull(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case IS_EMPTY:
                    return isEmpty(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case IS_NOT_EMPTY:
                    return !isEmpty(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case IS_BLANK:
                    return isBlank(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case IS_NOT_BLANK:
                    return !isBlank(getObjectByPath(ruleId, conditionId, propertyPath, object));
                case BEFORE:
                    return evaluateBefore(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
                case AFTER:
                    return evaluateAfter(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
                case ON:
                    return evaluateOn(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
                case LENGTH_EQUALS:
                    return evaluateLenEq(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
                case LENGTH_GREATER_THAN:
                    return evaluateLenGt(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
                case LENGTH_LESS_THAN:
                    return evaluateLenLt(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
                case MATCH:
                    return evaluateMatch(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getRegexFlags());
                case NOT_MATCH:
                    return !evaluateMatch(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getRegexFlags());
                 default:
                     // not a built-in operator
                     break;
            }
        }
        // lookup the custom operator if not a built in one.
        CustomOperator customOperator = customOperatorRegistry.get(opCode);
        if (customOperator != null) {
            return customOperator.evaluate(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValues);
        } else {
            String errorMsg = "Invalid operator! Operator code " + opCode + " is not supported";
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new InvalidOperatorException(errorMsg, ruleId, conditionId, opCode);
        }
    }

    public Object getObjectByPath(String ruleId, String conditionId, String propertyPath, Object object) throws RuleEngineException {
        if (object == null || propertyPath == null) {
            return object;
        }

        String key = propertyPath + ":" + System.identityHashCode(object);
        Map<String, Object> cache = objectCache.get();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }


        Object current = object;
        String[] properties = propertyPath.split("\\.");
        for (String subProperty : properties) {
            if (current == null) {
                break;
            } else if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(subProperty);
            } else if (current instanceof List) {
                try {
                    current = ((List<?>) current).get(Integer.parseInt(subProperty));
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    String errorMsg = "Number format exception or Index out of bound exception with object " + object + " in getObjectByPath for propertyPath " + propertyPath;
                    logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                    throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
                }
            } else {
                Class<?> clazz = current.getClass();
                Method[] accessors = getAccessors(clazz);
                boolean found = false;
                for (Method accessor : accessors) {
                    if (accessor.getName().substring(RuleConstants.GET_METHOD_PREFIX.length()).equals(capitalizeFirstLetter(subProperty))) {
                        try {
                            current = accessor.invoke(current, EMPTY_CLASS_LIST);
                            found = true;
                            break;
                        } catch (Exception e) {
                            String errorMsg = "Invocation error with object " + object + " in getObjectByPath for propertyPath " + propertyPath;
                            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
                        }
                    }
                }
                if(!found) {
                    return null;
                }
            }
        }
        cache.put(key, current);
        return current;
    }

    private boolean evaluateEquals(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        } else {
            TypeSpecificOperation typeSpecificOperation = typeStrategies.get(object.getClass());
            if (typeSpecificOperation == null) {
                return object.equals(valueObject);
            }
            return typeSpecificOperation.equals(object, valueObject);
        }
    }

    /**
     * Checks if string version of object is contained within ConditionValue
     */
    private boolean evaluateContains(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        // null contains null
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        }
        if (valueObject instanceof String) {
            return (object.toString().indexOf((String) valueObject) >= 0);
        } else {
            String errorMsg = "Contains evaluation with type different than string " + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            return false;
        }
    }


    /**
     * Checks if object value is contained in the list of condition values
     */
    private boolean evaluateInList(String ruleId, String conditionId, Object object, List valueConditions) throws RuleEngineException {
        // empty list contains null
        if (object == null || valueConditions == null || valueConditions.isEmpty()) {
            return (object == null
                    && (valueConditions == null || valueConditions.isEmpty()));
        }

        boolean match = false;
        ListIterator it = valueConditions.listIterator();
        while (!match && it.hasNext()) {
            RuleConditionValue value = (RuleConditionValue) it.next();
            Object valueObject = convertConditionValue(ruleId, conditionId, object, value.getConditionValue(), null);
            if (object == null || valueObject == null) {
                match = (object == null && valueObject == null);
            } else {
                match = object.equals(valueObject);
            }
        }
        return match;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateGreaterThan(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        return (compareNumeric(ruleId, conditionId, object, valueObject) > 0);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateGreaterThanOrEqual(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        return (compareNumeric(ruleId, conditionId, object, valueObject) >= 0);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateLessThan(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        return (compareNumeric(ruleId, conditionId, object, valueObject) < 0);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateLessThanOrEqual(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        return (compareNumeric(ruleId, conditionId, object, valueObject) <= 0);
    }

    /**
     * Compare two numeric values.
     */
    private int compareNumeric(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if (!(object instanceof Number)) {
            String errorMsg = "Object is not a number:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        TypeSpecificOperation typeSpecificOperation = typeStrategies.get(object.getClass());
        if (typeSpecificOperation == null) {
            return 0;
        }
        return typeSpecificOperation.compare(ruleId, conditionId, object, valueObject);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateBefore(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        return (compareDate(ruleId, conditionId, object, valueObject, dateFormat) < 0);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateAfter(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        return (compareDate(ruleId, conditionId, object, valueObject, dateFormat) > 0);
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateOn(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        return (compareDate(ruleId, conditionId, object, valueObject, dateFormat) == 0);
    }

    /**
     * Compare two Date values.
     */
    private int compareDate(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        TypeSpecificOperation typeSpecificOperation = typeStrategies.get(object.getClass());
        if (typeSpecificOperation == null) {
            return 0;
        }
        return typeSpecificOperation.compare(ruleId, conditionId, object, valueObject);
    }

    private boolean evaluateLenEq(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        if (valueObject == null) return false;
        return (compareStringLength(ruleId, conditionId, object, valueObject, valueTypeCode) == 0);
    }

    private boolean evaluateLenGt(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        if (valueObject == null) return false;
        return (compareStringLength(ruleId, conditionId, object, valueObject, valueTypeCode) > 0);
    }

    private boolean evaluateLenLt(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        if (valueObject == null) return false;
        return (compareStringLength(ruleId, conditionId, object, valueObject, valueTypeCode) < 0);
    }

    /**
     * Compare two string's length
     */
    private int compareStringLength(String ruleId, String conditionId, Object object, Object valueObject, String valueTypeCode) throws RuleEngineException {
        if(object == null) {
            if(valueObject == null) {
                return 0;   // null is equal to null
            } else {
                return -1;  // null is less than any string
            }
        }
        if (!(object instanceof java.lang.String)) {
            String errorMsg = "Object is not a String:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        TypeSpecificOperation typeSpecificOperation = typeStrategies.get(object.getClass());
        if (typeSpecificOperation == null) {
            return 0;
        }
        return typeSpecificOperation.compareLength(ruleId, conditionId, object, valueObject, valueTypeCode);
    }

    private boolean evaluateMatch(String ruleId, String conditionId, Object object, Object valueObject, String regexFlags) throws RuleEngineException {
        if(!(object instanceof java.lang.String)) {
            String errorMsg = "Object is not a String:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        Object value = valueObject;
        if(!(valueObject instanceof java.lang.String)) {
            value = valueObject.toString();
        }
        if(value != null && ((String)value).length() > 0) {
            String regex = (String)value;
            int flags = 0;
            if(regexFlags != null) {
                if(regexFlags.contains("i")) flags = flags | Pattern.CASE_INSENSITIVE;
                if(regexFlags.contains("m")) flags = flags | Pattern.MULTILINE;
                if(regexFlags.contains("s")) flags = flags | Pattern.DOTALL;
                if(regexFlags.contains("u")) flags = flags | Pattern.UNICODE_CASE;
                if(regexFlags.contains("x")) flags = flags | Pattern.COMMENTS;
            }
            final int finalFlags = flags; // Create a final variable to use in the lambda
            String key = regex + ":" + finalFlags;
            Pattern pattern = patternCache.computeIfAbsent(key, k -> Pattern.compile(regex, finalFlags));
            return pattern.matcher((String)object).matches();

        } else {
            String errorMsg = "Condition Value is empty";
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
    }

    /**
     * Check if the object is null
     */
    private boolean isNull(Object val) {
        return (val == null);
    }

    /**
     * Check if the string is null or empty
     */
    private boolean isEmpty(Object val) {
        return (val == null || val.toString().length() == 0);
    }

    /**
     * Check if the string is null or blank
     */
    private boolean isBlank(Object val) {
        return (val == null || val.toString().trim().length() == 0);
    }


    private Object convertConditionValue(String ruleId, String conditionId, Object object, String valueStr, String dateFormat) throws RuleEngineException {
        TypeSpecificOperation typeSpecificOperation = typeStrategies.get(object.getClass());
        if(typeSpecificOperation == null) {
            return null;
        }
        return typeSpecificOperation.convert(ruleId, conditionId, object, valueStr, dateFormat);
    }

    private Method[] getAccessors(Class clazz) {

        Method[] allMethods = (Method[]) accessorMap.get(clazz);
        if (allMethods == null) {
            allMethods = clazz.getMethods();
            ArrayList accessors = new ArrayList(allMethods.length);
            for (int i = 0; i < allMethods.length; i++) {
                allMethods[i].setAccessible(true);
                if (allMethods[i].getName().startsWith(RuleConstants.GET_METHOD_PREFIX)
                        && allMethods[i].getParameterTypes().length == 0) {
                    accessors.add(allMethods[i]);
                }
            }
            allMethods = (Method[]) accessors.toArray(new Method[accessors
                    .size()]);
            accessorMap.put(clazz, allMethods);
        }
        return allMethods;
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Return the original string if null or empty
        }
        // Capitalize the first letter and concatenate with the rest of the string
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
