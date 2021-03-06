package com.networknt.rule;

public class RuleConstants {
    /** Code for the EQUALS criteria operator */
    public static final String CR_OP_EQUALS = "EQ";
    /** Code for the NOT EQUAL criteria operator */
    public static final String CR_OP_NOT_EQUALS = "NEQ";
    /** Code for the CONTAINS criteria operator */
    public static final String CR_OP_CONTAINS = "CS";
    /** Code for the DOES NOT CONTAIN criteria operator */
    public static final String CR_OP_NOT_CONTAINS = "NCS";
    /** Code for the IN LIST criteria operator */
    public static final String CR_OP_IN_LIST = "IN";
    /** Code for the NOT IN LIST criteria operator */
    public static final String CR_OP_NOT_IN_LIST = "NIN";
    /** Code for the Greater Than criteria operator */
    public static final String CR_OP_GT = "GT";
    /** Code for the Less Than criteria operator */
    public static final String CR_OP_LT = "LT";
    /** Code for the Is Blank criteria operator */
    public static final String CR_OP_EMPTY = "NIL";
    /** Code for the Is Not Blank criteria operator */
    public static final String CR_OP_NOT_EMPTY = "NNIL";
    /** Code for the Date Before operator */
    public static final String CR_OP_BEFORE = "BF";
    /** Code for the Date After operator */
    public static final String CR_OP_AFTER = "AF";
    /** Code for the length of string greater than */
    public static final String CR_OP_LEN_GT = "LEN_GT";
    /** Code for the length of sting less than */
    public static final String CR_OP_LEN_LT = "LEN_LT";
    /** Code for the length of string equal */
    public static final String CR_OP_LEN_EQ = "LEN_EQ";
    /** Code for the string match */
    public static final String CR_OP_MATCH = "MATCH";
    /** Code for the string not match */
    public static final String CR_OP_NOT_MATCH = "NMATCH";

    /** Code for the AND criteria join operator */
    public static final String CR_JOIN_OP_AND = "AND";
    /** Code for the OR criteria join operator */
    public static final String CR_JOIN_OP_OR = "OR";
    /** Code for the NOT criteria join operator */
    public static final String CR_JOIN_OP_NOT = "NOT";

    /** Key of the result in the resultMap after the condition evaluation. */
    public static final String RESULT = "result";
    /** Key in the result map while exception happens */
    public static final String RULE_ENGINE_EXCEPTION = "RuleEngineException";
}
