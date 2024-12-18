package com.networknt.rule;

import com.networknt.rule.Rule;
import com.networknt.rule.RuleCondition;
import com.networknt.rule.RuleConditionValue;
import com.networknt.rule.RuleEvaluator;
import com.networknt.rule.exception.RuleEngineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RuleEvaluatorRegexCacheTest {
    private RuleEvaluator ruleEvaluator;
    private Rule rule;
    private Map<String, Object> objMap;
    private RuleCondition condition;
    private RuleConditionValue conditionValue;
    private String ruleId;
    private String conditionId;
    private Pattern mockPattern;
    private Matcher mockMatcher;
    private TestObject testObject;


    @BeforeEach
    void setup() {
        ruleEvaluator = RuleEvaluator.getInstance();
        ruleId = "test-rule";
        conditionId = "test-condition";
        rule = new Rule();
        rule.setRuleId(ruleId);
        objMap = new HashMap<>();
        condition = new RuleCondition();
        condition.setConditionId(conditionId);
        conditionValue = new RuleConditionValue();
        mockPattern = Mockito.mock(Pattern.class);
        mockMatcher = Mockito.mock(Matcher.class);
        when(mockMatcher.matches()).thenReturn(true);
        testObject = new TestObject();
        testObject.setName("test-string");


    }
    @Test
    void testRegexCacheHit() throws Exception {
        doReturn(mockMatcher).when(mockPattern).matcher(anyString());
        try (var mockedStatic = Mockito.mockStatic(Pattern.class)) {
            mockedStatic.when(() -> Pattern.compile("test.*", 0)).thenReturn(mockPattern);
            condition.setPropertyPath("name");
            condition.setOperatorCode(RuleOperator.MATCH.getOperator());
            conditionValue.setConditionValue("test.*");
            List<RuleConditionValue> conditionValues = new ArrayList<>();
            conditionValues.add(conditionValue);

            // call evaluateMatch twice with same regex and flags
            Object result1 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "Name", RuleOperator.MATCH.getOperator(), conditionValues, testObject);
            Object result2 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "Name", RuleOperator.MATCH.getOperator(), conditionValues, testObject);

            assertTrue((Boolean)result1);
            assertTrue((Boolean)result2);

            // Pattern.compile should be called only one time. but matcher should be called twice.
            mockedStatic.verify(() -> Pattern.compile("test.*", 0), times(1));
            verify(mockPattern, times(2)).matcher("test-string");

        }

    }

    @Test
    @Disabled
    void testRegexCacheWithDifferentFlags() throws Exception {
        try (var mockedStatic = Mockito.mockStatic(Pattern.class)) {
            doReturn(mockMatcher).when(mockPattern).matcher(anyString());
            mockedStatic.when(() -> Pattern.compile("test.*", Pattern.CASE_INSENSITIVE)).thenReturn(mockPattern);
            Pattern mockPattern2 = Mockito.mock(Pattern.class);
            Matcher mockMatcher2 = Mockito.mock(Matcher.class);
            doReturn(mockMatcher2).when(mockPattern2).matcher(anyString());
            mockedStatic.when(() -> Pattern.compile("test.*", 0)).thenReturn(mockPattern2);

            condition.setPropertyPath("name");
            condition.setOperatorCode(RuleOperator.MATCH.getOperator());
            conditionValue.setConditionValue("test.*");
            conditionValue.setRegexFlags("i");
            List<RuleConditionValue> conditionValues = new ArrayList<>();
            conditionValues.add(conditionValue);

            Object result1 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "Name", RuleOperator.MATCH.getOperator(), conditionValues, testObject);
            RuleConditionValue conditionValue2 = new RuleConditionValue();
            conditionValue2.setConditionValue("test.*");
            List<RuleConditionValue> conditionValues2 = new ArrayList<>();
            conditionValues2.add(conditionValue2);

            Object result2 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "Name", RuleOperator.MATCH.getOperator(), conditionValues2, testObject);


            assertTrue((Boolean)result1);
            assertTrue((Boolean)result2);

            // Pattern.compile should be called two time since it has different flags.
            mockedStatic.verify(() -> Pattern.compile("test.*", Pattern.CASE_INSENSITIVE), times(1));
            mockedStatic.verify(() -> Pattern.compile("test.*", 0), times(1));
            verify(mockPattern, times(1)).matcher(anyString());
            verify(mockPattern2, times(1)).matcher(anyString());
        }
    }

    /**
     * the regex cache is shared across the thread in RuleEvaluator and you are calling the evaluate method before the evaluateCondition.
     * The evaluate method is also using the regex cache and therefore, the regex is already cached when calling the evaluateCondition
     * for the second time.
     * @throws Exception exception
     */
    @Test
    @Disabled
    void testRegexCacheClearBetweenEvaluations() throws Exception {
        try (var mockedStatic = Mockito.mockStatic(Pattern.class)) {
            doReturn(mockMatcher).when(mockPattern).matcher(anyString());
            mockedStatic.when(() -> Pattern.compile("test.*", 0)).thenReturn(mockPattern);

            condition.setPropertyPath("name");
            condition.setOperatorCode(RuleOperator.MATCH.getOperator());
            conditionValue.setConditionValue("test.*");
            List<RuleConditionValue> conditionValues = new ArrayList<>();
            conditionValues.add(conditionValue);


            // First evaluation
            Object result1 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "name", RuleOperator.MATCH.getOperator(), conditionValues, testObject);
            assertTrue((Boolean)result1);

            // Second Evaluation
            Object result2 = ruleEvaluator.evaluateCondition(ruleId, conditionId, "name", RuleOperator.MATCH.getOperator(), conditionValues, testObject);
            assertTrue((Boolean)result2);


            // Pattern.compile should be called only one time since the pattern cache is shared across threads.
            mockedStatic.verify(() -> Pattern.compile("test.*", 0), times(1));
            verify(mockPattern, times(2)).matcher("test-string");
        }
    }

    static class TestObject {
        String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

}