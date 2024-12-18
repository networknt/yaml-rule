package com.networknt.rule;

import com.networknt.rule.exception.ConditionEvaluationException;
import com.networknt.rule.exception.InvalidOperatorException;
import com.networknt.rule.exception.RuleEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class RuleEvaluator {
    private static RuleEvaluator instance = null;

    /**
     * Logger instance for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(RuleEvaluator.class);

    private static final Map accessorMap = new HashMap();
    private static final Class[] EMPTY_CLASS_LIST = new Class[0];

    private static final String ACCESSOR_METHOD_PREFIX = "get";

    private static HashSet simpleTypes;

    private static final ThreadLocal<Map<String, Object>> objectCache = ThreadLocal.withInitial(HashMap::new);

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
    private boolean evaluateConditionExpression(String ruleId, String expression, Collection<RuleCondition> conditions, Map objMap, Map resultMap) throws RuleEngineException {
        // A simple stack-based parser for expressions like "(cid1 OR (cid2 AND cid3))"
        Stack<Boolean> stack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "() ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue; // Skip empty tokens

            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    processOperator(ruleId, stack, operatorStack, objMap, resultMap, conditions);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                    operatorStack.pop();
                }
            } else if (token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") && hasHigherPrecedence(operatorStack.peek(), token)) {
                    processOperator(ruleId, stack, operatorStack, objMap, resultMap, conditions);
                }
                operatorStack.push(token);
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
                stack.push(conditionResult);
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
        return stack.pop();
    }


    private boolean hasHigherPrecedence(String op1, String op2) {
        if (op1.equals("(") || op1.equals(")")) {
            return false;
        }
        if (op2.equals("(") || op2.equals(")")) {
            return true;
        }
        return op1.equalsIgnoreCase("AND") && op2.equalsIgnoreCase("OR");
    }

    private void processOperator(String ruleId, Stack<Boolean> stack, Stack<String> operatorStack, Map objMap, Map resultMap, Collection<RuleCondition> conditions) throws RuleEngineException {
        String operator = operatorStack.pop();
        if (operator.equalsIgnoreCase("AND") || operator.equalsIgnoreCase("OR")) {
            if (stack.size() < 2) {
                String errorMsg = "Invalid expression. Stack size " + stack.size() + " is less than 2 for an operator " + operator;
                logger.error("Error evaluating condition in rule {}: {}", ruleId, errorMsg);
                throw new RuleEngineException(errorMsg, ruleId);
            }
            boolean operand2 = stack.pop();
            boolean operand1 = stack.pop();
            if (operator.equalsIgnoreCase("AND")) {
                stack.push(operand1 && operand2);
            } else if (operator.equalsIgnoreCase("OR")) {
                stack.push(operand1 || operand2);
            }
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
    private boolean evaluateCondition(String ruleId, String conditionId, String propertyPath,
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
        if (RuleConstants.CR_OP_EQUALS.equals(opCode)) {
            return evaluateEquals(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NOT_EQUALS.equals(opCode)) {
            return !evaluateEquals(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_CONTAINS.equals(opCode)) {
            return evaluateContains(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NOT_CONTAINS.equals(opCode)) {
            return !evaluateContains(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_IN_LIST.equals(opCode)) {
            return evaluateInList(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), conditionValues);
        } else if (RuleConstants.CR_OP_NOT_IN_LIST.equals(opCode)) {
            return !evaluateInList(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), conditionValues);
        } else if (RuleConstants.CR_OP_GT.equals(opCode)) {
            return evaluateGreaterThan(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_GTE.equals(opCode)) {
            return evaluateGreaterThanOrEqual(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_LT.equals(opCode)) {
            return evaluateLessThan(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_LTE.equals(opCode)) {
            return evaluateLessThanOrEqual(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NULL.equals(opCode)) {
            return isNull(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_NOT_NULL.equals(opCode)) {
            return !isNull(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_EMPTY.equals(opCode)) {
            return isEmpty(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_NOT_EMPTY.equals(opCode)) {
            return !isEmpty(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_BLANK.equals(opCode)) {
            return isBlank(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_NOT_BLANK.equals(opCode)) {
            return !isBlank(getObjectByPath(ruleId, conditionId, propertyPath, object));
        } else if (RuleConstants.CR_OP_BEFORE.equals(opCode)) {
            return evaluateBefore(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
        } else if (RuleConstants.CR_OP_AFTER.equals(opCode)) {
            return evaluateAfter(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
        } else if (RuleConstants.CR_OP_ON.equals(opCode)) {
            return evaluateOn(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getDateFormat());
        } else if (RuleConstants.CR_OP_LEN_EQ.equals(opCode)) {
            return evaluateLenEq(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_LEN_GT.equals(opCode)) {
            return evaluateLenGt(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_LEN_LT.equals(opCode)) {
            return evaluateLenLt(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_MATCH.equals(opCode)) {
            return evaluateMatch(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getRegexFlags());
        } else if (RuleConstants.CR_OP_NOT_MATCH.equals(opCode)) {
            return !evaluateMatch(ruleId, conditionId, getObjectByPath(ruleId, conditionId, propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getRegexFlags());
        } else {
            String errorMsg = "Invalid operator! Operator code " + opCode + " is not supported";
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new InvalidOperatorException(errorMsg, ruleId, conditionId, opCode);
        }
    }

    public Object getObjectByPath(String ruleId, String conditionId, String propertyPath, Object object) throws RuleEngineException {
        if (object == null) {
            return null;
        } else {
            if (propertyPath == null) {
                return object;
            }
        }

        boolean isLastProp = true;
        String subProperty;
        int index = propertyPath.indexOf(".");
        if (index > 0) {
            isLastProp = false;
            subProperty = propertyPath.substring(0, index);
        } else {
            subProperty = propertyPath;
        }
        if (object instanceof Map) {
            // in case of map, the path is the key
            Object o = ((Map) object).get(subProperty);
            if (isLastProp) {
                return o;
            } else {
                return getObjectByPath(ruleId, conditionId, propertyPath.substring(subProperty.length() + 1), o);
            }
        } else if (object instanceof List) {
            // in case of list, the path is the index
            Object o = ((List) object).get(Integer.valueOf(subProperty));
        } else {
            // normal java object that needs to get the methods to access.
            Class clazz = object.getClass();
            Method[] accessors = getAccessors(clazz);
            for (int i = 0; i < accessors.length; i++) {

                if (accessors[i].getName().substring(ACCESSOR_METHOD_PREFIX.length()).equals(subProperty)) {
                    // method name matched.
                    try {
                        Object o = accessors[i].invoke(object, EMPTY_CLASS_LIST);
                        if (isLastProp) {
                            return o;
                        } else {
                            return getObjectByPath(ruleId, conditionId, propertyPath.substring(subProperty.length() + 1), o);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Invocation error with object " + object + " in getObjectByPath for propertyPath " + propertyPath;
                        logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                        throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
                    }
                }
            }
        }
        return null;
    }

    private boolean evaluateEquals(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        } else {
            if (valueObject instanceof String) {
                Object convertedValueObject = convertConditionValue(ruleId, conditionId, object, (String) valueObject, null);
                return object.equals(convertedValueObject);
            } else {
                return object.equals(valueObject);
            }
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
        boolean result = (compareNumeric(ruleId, conditionId, object, valueObject) > 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateGreaterThanOrEqual(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        boolean result = (compareNumeric(ruleId, conditionId, object, valueObject) >= 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateLessThan(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        boolean result = (compareNumeric(ruleId, conditionId, object, valueObject) < 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateLessThanOrEqual(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {
        boolean result = (compareNumeric(ruleId, conditionId, object, valueObject) <= 0);
        return result;
    }

    /**
     * Compare two numeric values.
     */
    private int compareNumeric(String ruleId, String conditionId, Object object, Object valueObject) throws RuleEngineException {

        if (!(object instanceof java.lang.Number)) {
            String errorMsg = "Object is not a number:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        if (evaluateEquals(ruleId, conditionId, object, valueObject)) {
            return 0;
        }

        if (object == null && valueObject == null) {
            return 0;
        }
        if (object == null && valueObject != null) {
            return -1;
        }
        if (object != null && valueObject == null) {
            return 1;
        }
        Object value = valueObject;
        if (valueObject instanceof String) {
            value = convertConditionValue(ruleId, conditionId, object, (String) valueObject, null);
        }

        if (value == null && object == null) {
            return 0;
        }
        if (value != null && object == null) {
            return -1;
        }
        if (value == null && object != null) {
            return 1;
        }

        if (object.getClass().getName().equals("java.lang.Integer")) {
            return ((Integer) object).compareTo((Integer) value);
        }
        if (object.getClass().getName().equals("java.math.BigDecimal")) {
            return ((BigDecimal) object).compareTo((BigDecimal) value);
        }
        if (object.getClass().getName().equals("java.lang.Long")) {
            return ((Long) object).compareTo((Long) value);
        }
        if (object.getClass().getName().equals("java.math.BigInteger")) {
            return ((BigInteger) object).compareTo((BigInteger) value);
        }
        if (object.getClass().getName().equals("java.lang.Byte")) {
            return ((Byte) object).compareTo((Byte) value);
        }
        if (object.getClass().getName().equals("java.lang.Double")) {
            return ((Double) object).compareTo((Double) value);
        }
        if (object.getClass().getName().equals("java.lang.Float")) {
            return ((Float) object).compareTo((Float) value);
        }
        if (object.getClass().getName().equals("java.lang.Short")) {
            return ((Short) object).compareTo((Short) value);
        }
        return 0;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateBefore(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        boolean result = (compareDate(ruleId, conditionId, object, valueObject, dateFormat) < 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateAfter(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {

        boolean result = (compareDate(ruleId, conditionId, object, valueObject, dateFormat) > 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateOn(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {
        boolean result = (compareDate(ruleId, conditionId, object, valueObject, dateFormat) == 0);
        return result;
    }

    /**
     * Compare two Date values.
     */
    private int compareDate(String ruleId, String conditionId, Object object, Object valueObject, String dateFormat) throws RuleEngineException {

        if (!(object instanceof java.util.Date)) {
            String errorMsg = "Object is not a Date";
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }

        if (evaluateEquals(ruleId, conditionId, object, valueObject)) {
            return 0;
        }

        if (object == null && valueObject == null) {
            return 0;
        }
        if (object == null && valueObject != null) {
            return -1;
        }
        if (object != null && valueObject == null) {
            return 1;
        }
        Object value = valueObject;
        if (valueObject instanceof String) {
            value = convertConditionValue(ruleId, conditionId, object, (String) valueObject, dateFormat);
        }

        if (value == null && object == null) {
            return 0;
        }
        if (value != null && object == null) {
            return -1;
        }
        if (value == null && object != null) {
            return 1;
        }

        if (object.getClass().getName().equals("java.util.Date")) {
            return ((java.util.Date) object).compareTo((Date) value);
        }
        if (object.getClass().getName().equals("java.sql.Date")) {
            return ((java.sql.Date) object).compareTo((Date) value);
        }
        if (object.getClass().getName().equals("java.sql.Timestamp")) {
            return ((Timestamp) object).compareTo((Timestamp) value);
        }
        return 0;
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

        if (!(object instanceof java.lang.String)) {
            String errorMsg = "Object is not a String:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        if (evaluateEquals(ruleId, conditionId, object, valueObject)) {
            return 0;
        }

        if (object == null && valueObject == null) {
            return 0;
        }
        if (object == null && valueObject != null) {
            return -1;
        }
        if (object != null && valueObject == null) {
            return 1;
        }

        if (valueObject == null && object == null) {
            return 0;
        }
        if (valueObject != null && object == null) {
            return -1;
        }
        if (valueObject == null && object != null) {
            return 1;
        }

        Object value = valueObject;
        if ("STRING".equals(valueTypeCode)) {
            if (!(valueObject instanceof String)) {
                value = valueObject.toString();
            }
            if (object.getClass().getName().equals("java.lang.String")) {
                return ((java.lang.String) object).length() - ((String) value).length();
            } else {
                String errorMsg = "Incompatible object to compare length of String with " + object.getClass().getName();
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }
        } else if ("INTEGER".equals(valueTypeCode)) {
            if (object.getClass().getName().equals("java.lang.String")) {
                return ((java.lang.String) object).length() - Integer.valueOf(value.toString());
            } else {
                String errorMsg = "Incompatible object to compare length of String with " + object.getClass().getName();
                logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }
        } else {
            String errorMsg = "Incompatible value type " + valueTypeCode;
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
    }

    private boolean evaluateMatch(String ruleId, String conditionId, Object object, Object valueObject, String regexFlags) throws RuleEngineException {
        if (!(object instanceof java.lang.String)) {
            String errorMsg = "Object is not a String:" + object.getClass();
            logger.error("Error evaluating condition in rule {}, condition {}: {}", ruleId, conditionId, errorMsg);
            throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
        }
        Object value = valueObject;
        if (!(valueObject instanceof java.lang.String)) {
            value = valueObject.toString();
        }
        if (value != null && ((String) value).length() > 0) {
            int flags = 0;
            if (regexFlags != null) {
                if (regexFlags.contains("i")) flags = flags | Pattern.CASE_INSENSITIVE;
                if (regexFlags.contains("m")) flags = flags | Pattern.MULTILINE;
                if (regexFlags.contains("s")) flags = flags | Pattern.DOTALL;
                if (regexFlags.contains("u")) flags = flags | Pattern.UNICODE_CASE;
                if (regexFlags.contains("x")) flags = flags | Pattern.COMMENTS;
            }
            return Pattern.compile((String) value, flags).matcher((String) object).matches();
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
        Class clazz = object.getClass();
        Object oret = null;
        if (clazz.getName().equals("java.lang.String")) {
            oret = valueStr;
        }
        if (clazz.getName().equals("java.lang.Integer")) {
            oret = Integer.valueOf(valueStr);
        }
        if (clazz.getName().equals("java.math.BigDecimal")) {
            oret = new BigDecimal(valueStr);
        }
        if (clazz.getName().equals("java.math.BigInteger")) {
            oret = new BigInteger(valueStr);
        }
        if (clazz.getName().equals("java.lang.Long")) {
            oret = Long.valueOf(valueStr);
        }
        if (clazz.getName().equals("java.lang.Boolean")) {
            oret = new Boolean(valueStr);
        }
        if (clazz.getName().equals("java.lang.Byte")) {
            oret = Byte.valueOf(valueStr);
        }
        if (clazz.getName().equals("java.lang.Character")) {
            oret = new Character(valueStr.charAt(0));
        }
        if (clazz.getName().equals("java.lang.Double")) {
            oret = new Double(valueStr);
        }
        if (clazz.getName().equals("java.lang.Float")) {
            oret = new Float(valueStr);
        }
        if (clazz.getName().equals("java.lang.Short")) {
            oret = new Short(valueStr);
        }
        if (clazz.getName().equals("java.sql.Timestamp")) {
            oret = Timestamp.valueOf(valueStr);
        }
        if (clazz.getName().equals("java.util.Date")) {
            DateFormat df = null;
            if (dateFormat != null) {
                df = new SimpleDateFormat(dateFormat);
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            try {
                oret = df.parse(valueStr);
            } catch (ParseException e) {
                String errorMsg = "Error parsing date string " + valueStr + " with format " + (dateFormat != null ? dateFormat : "yyyy-MM-dd HH:mm:ss");
                logger.error("Error parsing date string in rule {}, condition {}: {}", ruleId, conditionId, errorMsg, e);
                throw new ConditionEvaluationException(errorMsg, ruleId, conditionId);
            }
        }
        if (clazz.getName().equals("java.sql.Date")) {
            oret = java.sql.Date.valueOf(valueStr);
        }
        return oret;
    }

    private Method[] getAccessors(Class clazz) {

        Method[] allMethods = (Method[]) accessorMap.get(clazz);
        if (allMethods == null) {
            allMethods = clazz.getMethods();
            ArrayList accessors = new ArrayList(allMethods.length);
            for (int i = 0; i < allMethods.length; i++) {
                allMethods[i].setAccessible(true);
                if (allMethods[i].getName().startsWith(ACCESSOR_METHOD_PREFIX)
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

}
