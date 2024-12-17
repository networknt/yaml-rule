package com.networknt.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class RuleEvaluator {
    private static RuleEvaluator instance = null;

    /** Logger instance for this class */
    private static Logger logger = LoggerFactory.getLogger(RuleEvaluator.class);

    private static Map accessorMap = new HashMap();
    private static final Class[] EMPTY_CLASS_LIST = new Class[0];

    private static final String ACCESSOR_METHOD_PREFIX = "get";

    private static HashSet simpleTypes;

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
     * @param rule rule
     * @param objMap input map
     * @return true if the rule matches the orphan, false otherwise.
     * @throws Exception
     *             if could not evaluate
     */
    public boolean evaluate(Rule rule, Map objMap, Map resultMap) throws Exception {

        boolean result = true;

        Collection conditions = rule.getConditions();
        if (conditions != null && !conditions.isEmpty()) {
            Iterator it = conditions.iterator();

            while (it.hasNext()) {
                RuleCondition rc = (RuleCondition) it.next();
                boolean r = evaluateCondition(rc.getPropertyPath(), rc.getOperatorCode(),
                        (List) rc.getConditionValues(), objMap);
                resultMap.put(rc.getConditionId(), r);
                result = evaluateJoin(rc.getJoinCode(), result, r);
            }
        }
        return result;
    }


    /**
     * Evaluates joint result of the 2 conditions from the rule.
     *
     */
    private boolean evaluateJoin(
            String opCode, boolean cr1Result, boolean cr2Result) throws Exception {

        if (RuleConstants.CR_JOIN_OP_AND.equals(opCode)) {
            return (cr1Result && cr2Result);
        } else if (RuleConstants.CR_JOIN_OP_OR.equals(opCode)) {
            return (cr1Result || cr2Result);
        } else if (RuleConstants.CR_JOIN_OP_NOT.equals(opCode)) {
            return (cr1Result && !cr2Result);
        } else {
            logger.error("Condition join operator " + opCode + " is not supported");
            throw new Exception("Condition join operator " + opCode + " is not supported");
        }
    }

    /**
     * Evaluates values passed as strings based on their type and the operator.
     *
     */
    private boolean evaluateCondition(String propertyPath,
                                      String opCode, List conditionValues, Object object) throws Exception {
        if(object == null) {
            logger.warn("Input object is null for operator " + opCode);
            throw new Exception("Input object is null for operator " + opCode);
        }
        Object valueObject = null;
        RuleConditionValue conditionValue = null;
        if(conditionValues != null && !conditionValues.isEmpty()) {
            conditionValue = (RuleConditionValue) conditionValues.get(0);
            if(conditionValue != null) {
                if (conditionValue.isExpression()) {
                    valueObject = getObjectByPath(conditionValue.getConditionValue(), object);
                } else {
                    valueObject = conditionValue.getConditionValue();
                }
            }
        }
        if (RuleConstants.CR_OP_EQUALS.equals(opCode)) {
            return evaluateEquals(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NOT_EQUALS.equals(opCode)) {
            return !evaluateEquals(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_CONTAINS.equals(opCode)) {
            return evaluateContains(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NOT_CONTAINS.equals(opCode)) {
            return !evaluateContains(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_IN_LIST.equals(opCode)) {
            return evaluateInList(getObjectByPath(propertyPath, object), conditionValues);
        } else if (RuleConstants.CR_OP_NOT_IN_LIST.equals(opCode)) {
            return !evaluateInList(getObjectByPath(propertyPath, object), conditionValues);
        } else if (RuleConstants.CR_OP_GT.equals(opCode)) {
            return evaluateGreaterThan(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_LT.equals(opCode)) {
            return evaluateLessThan(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NULL.equals(opCode)) {
            return isNull(getObjectByPath(propertyPath, object));
        } else if (RuleConstants.CR_OP_NOT_NULL.equals(opCode)) {
            return !isNull(getObjectByPath(propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_EMPTY.equals(opCode)) {
            return isEmpty(getObjectByPath(propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_NOT_EMPTY.equals(opCode)) {
            return !isEmpty(getObjectByPath(propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_BLANK.equals(opCode)) {
            return isBlank(getObjectByPath(propertyPath, object));
        } else if (RuleConstants.CR_OP_IS_NOT_BLANK.equals(opCode)) {
            return !isBlank(getObjectByPath(propertyPath, object));
        }else if (RuleConstants.CR_OP_BEFORE.equals(opCode)) {
            return evaluateBefore(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_AFTER.equals(opCode)) {
            return evaluateAfter(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_LEN_EQ.equals(opCode)) {
            return evaluateLenEq(getObjectByPath(propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_LEN_GT.equals(opCode)) {
            return evaluateLenGt(getObjectByPath(propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_LEN_LT.equals(opCode)) {
            return evaluateLenLt(getObjectByPath(propertyPath, object), valueObject, conditionValue == null ? null : conditionValue.getValueTypeCode());
        } else if (RuleConstants.CR_OP_MATCH.equals(opCode)) {
            return evaluateMatch(getObjectByPath(propertyPath, object), valueObject);
        } else if (RuleConstants.CR_OP_NOT_MATCH.equals(opCode)) {
            return !evaluateMatch(getObjectByPath(propertyPath, object), valueObject);
        } else {
            logger.warn("Operator " + opCode + " is not supported");
            throw new Exception("Invalid operator" + opCode);
        }
    }

    public Object getObjectByPath(String propertyPath, Object object) {
        if(object == null) {
            return null;
        } else {
            if(propertyPath == null) {
                return object;
            }
        }

        boolean isLastProp = true;
        String subProperty;
        int index = propertyPath.indexOf(".");
        if(index > 0) {
            isLastProp = false;
            subProperty = propertyPath.substring(0, index);
        } else {
            subProperty = propertyPath;
        }
        if(object instanceof Map) {
            // in case of map, the path is the key
            Object o = ((Map) object).get(subProperty);
            if (isLastProp) {
                return o;
            } else {
                return getObjectByPath(propertyPath.substring(subProperty.length() + 1), o);
            }
        } else if(object instanceof List) {
            // in case of list, the path is the index
            Object o = ((List)object).get(Integer.valueOf(subProperty));
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
                            return getObjectByPath(propertyPath.substring(subProperty.length() + 1), o);
                        }
                    } catch (Exception e) {
                        logger.error("invoke error", e);
                    }
                }
            }
        }
        return null;
    }

    private boolean evaluateEquals(Object object, Object valueObject) throws Exception {
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        } else {
            if(valueObject instanceof String) {
                Object convertedValueObject = convertConditionValue(object, (String)valueObject);
                return object.equals(convertedValueObject);
            } else {
                return object.equals(valueObject);
            }
        }
    }

    /**
     * Checks if string version of object is contained within ConditionValue
     *
     */
    private boolean evaluateContains(Object object, Object valueObject) throws Exception {
        // null contains null
        if (object == null || valueObject == null) {
            return (object == null && valueObject == null);
        }
        if(valueObject instanceof String) {
            return (object.toString().indexOf((String)valueObject) >= 0);
        } else {
            logger.error("Contains evaluation with type different than string " + object.getClass());
            return false;
        }
    }


    /**
     * Checks if object value is contained in the list of condition values
     *
     */
    private boolean evaluateInList(Object object, List valueConditions) throws Exception {
        // empty list contains null
        if (object == null || valueConditions == null || valueConditions.isEmpty()) {
            return (object == null
                    && (valueConditions == null || valueConditions.isEmpty()));
        }

        boolean match = false;
        ListIterator it = valueConditions.listIterator();
        while (!match && it.hasNext()) {
            RuleConditionValue value = (RuleConditionValue) it.next();
            Object valueObject = convertConditionValue(object, value.getConditionValue());
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
    private boolean evaluateGreaterThan(Object object, Object valueObject) throws Exception {
        boolean result = (compareNumeric(object, valueObject) > 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateLessThan(Object object, Object valueObject) throws Exception {
        boolean result = (compareNumeric(object, valueObject) < 0);
        return result;
    }

    /**
     * Compare two numeric values.
     *
     */
    private int compareNumeric(Object object, Object valueObject) throws Exception {

        if(!(object instanceof java.lang.Number)) {
            throw new Exception("Object is not a number:" + object.getClass());
        }
        if(evaluateEquals(object, valueObject)) {
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
        if(valueObject instanceof String) {
            value = convertConditionValue(object, (String) valueObject);
        }

        if (value == null && object == null) {
            return 0;
        }
        if ( value != null && object == null) {
            return -1;
        }
        if ( value == null && object != null) {
            return 1;
        }

        if(object.getClass().getName().equals("java.lang.Integer")) {
            return ((Integer)object).compareTo((Integer) value);
        }
        if(object.getClass().getName().equals("java.math.BigDecimal")) {
            return ((BigDecimal)object).compareTo((BigDecimal) value);
        }
        if(object.getClass().getName().equals("java.lang.Long")) {
            return ((Long)object).compareTo((Long) value);
        }
        if(object.getClass().getName().equals("java.math.BigInteger")) {
            return ((BigInteger)object).compareTo((BigInteger) value);
        }
        if(object.getClass().getName().equals("java.lang.Byte")) {
            return ((Byte)object).compareTo((Byte) value);
        }
        if(object.getClass().getName().equals("java.lang.Double")) {
            return ((Double)object).compareTo((Double) value);
        }
        if(object.getClass().getName().equals("java.lang.Float")) {
            return ((Float)object).compareTo((Float) value);
        }
        if(object.getClass().getName().equals("java.lang.Short")) {
            return ((Short)object).compareTo((Short) value);
        }
        return 0;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateBefore(Object object, Object valueObject) throws Exception {
        boolean result = (compareDate(object, valueObject) < 0);
        return result;
    }

    /**
     * Evaluates whether the object value matches the criteria.
     */
    private boolean evaluateAfter(Object object, Object valueObject) throws Exception {

        boolean result = (compareDate(object, valueObject) > 0);
        return result;
    }

    /**
     * Compare two Date values.
     *
     */
    private int compareDate(Object object, Object valueObject) throws Exception {

        if(!(object instanceof java.util.Date)) {
            throw new Exception("Object is not a Date");
        }

        if(evaluateEquals(object, valueObject)) {
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
        if(valueObject instanceof String) {
            value = convertConditionValue(object, (String)valueObject);
        }

        if (value == null && object == null) {
            return 0;
        }
        if ( value != null && object == null) {
            return -1;
        }
        if ( value == null && object != null) {
            return 1;
        }

        if(object.getClass().getName().equals("java.util.Date")) {
            return ((java.util.Date)object).compareTo((Date) value);
        }
        if(object.getClass().getName().equals("java.sql.Date")) {
            return ((java.sql.Date)object).compareTo((Date) value);
        }
        if(object.getClass().getName().equals("java.sql.Timestamp")) {
            return ((Timestamp)object).compareTo((Timestamp) value);
        }
        return 0;
    }

    private boolean evaluateLenEq(Object object, Object valueObject, String valueTypeCode) throws Exception {
        if (valueObject == null) return false;
        return (compareStringLength(object, valueObject, valueTypeCode) == 0);
    }

    private boolean evaluateLenGt(Object object, Object valueObject, String valueTypeCode) throws Exception {
        if (valueObject == null) return false;
        return (compareStringLength(object, valueObject, valueTypeCode) > 0);
    }

    private boolean evaluateLenLt(Object object, Object valueObject, String valueTypeCode) throws Exception {
        if (valueObject == null) return false;
        return (compareStringLength(object, valueObject, valueTypeCode) < 0);
    }

    /**
     * Compare two string's length
     *
     */
    private int compareStringLength(Object object, Object valueObject, String valueTypeCode) throws Exception {

        if(!(object instanceof java.lang.String)) {
            throw new Exception("Object is not a String:" + object.getClass());
        }
        if(evaluateEquals(object, valueObject)) {
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
        if ( valueObject != null && object == null) {
            return -1;
        }
        if ( valueObject == null && object != null) {
            return 1;
        }

        Object value = valueObject;
        if("STRING".equals(valueTypeCode)) {
            if(!(valueObject instanceof String)) {
                value = valueObject.toString();
            }
            if(object.getClass().getName().equals("java.lang.String")) {
                return ((java.lang.String)object).length() - ((String)value).length();
            } else {
                throw new Exception("Incompatible object to compare length of String with " + object.getClass().getName());
            }
        } else if ("INTEGER".equals(valueTypeCode)) {
            if(object.getClass().getName().equals("java.lang.String")) {
                return ((java.lang.String)object).length() - Integer.valueOf(value.toString());
            } else {
                throw new Exception("Incompatible object to compare length of String with " + object.getClass().getName());
            }
        } else {
           throw new Exception("Incompatible value type " + valueTypeCode);
        }
    }

    private boolean evaluateMatch(Object object, Object valueObject) throws Exception {
        if(!(object instanceof java.lang.String)) {
            throw new Exception("Object is not a String:" + object.getClass());
        }
        Object value = valueObject;
        if(!(valueObject instanceof java.lang.String)) {
            value = valueObject.toString();
        }
        if(value != null && ((String)value).length() > 0) {
            return Pattern.matches((String)value, (String)object);
        } else {
            throw new Exception("Condition Value is empty");
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


    private Object convertConditionValue(Object object, String valueStr) throws Exception {
        Class clazz = object.getClass();
        Object oret = null;
        if(clazz.getName().equals("java.lang.String")) {
            oret = valueStr;
        }
        if(clazz.getName().equals("java.lang.Integer")) {
            oret = Integer.valueOf(valueStr);
        }
        if(clazz.getName().equals("java.math.BigDecimal")) {
            oret = new BigDecimal(valueStr);
        }
        if(clazz.getName().equals("java.math.BigInteger")) {
            oret = new BigInteger(valueStr);
        }
        if(clazz.getName().equals("java.lang.Long")) {
            oret = Long.valueOf(valueStr);
        }
        if(clazz.getName().equals("java.lang.Boolean")) {
            oret = new Boolean(valueStr);
        }
        if(clazz.getName().equals("java.lang.Byte")) {
            oret = Byte.valueOf(valueStr);
        }
        if(clazz.getName().equals("java.lang.Character")) {
            oret = new Character(valueStr.charAt(0));
        }
        if(clazz.getName().equals("java.lang.Double")) {
            oret = new Double(valueStr);
        }
        if(clazz.getName().equals("java.lang.Float")) {
            oret = new Float(valueStr);
        }
        if(clazz.getName().equals("java.lang.Short")) {
            oret = new Short(valueStr);
        }
        if(clazz.getName().equals("java.sql.Timestamp")) {
            oret = Timestamp.valueOf(valueStr);
        }
        if(clazz.getName().equals("java.util.Date")) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            oret = df.parse(valueStr);
        }
        if(clazz.getName().equals("java.sql.Date")) {
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
