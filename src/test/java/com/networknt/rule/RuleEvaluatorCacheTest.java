package com.networknt.rule;

import com.networknt.rule.Rule;
import com.networknt.rule.RuleCondition;
import com.networknt.rule.RuleConditionValue;
import com.networknt.rule.RuleEvaluator;
import com.networknt.rule.exception.RuleEngineException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Explanation:
 *
 * RuleEvaluatorCacheTest: This is the test class.
 *
 * setup(): Sets up the test objects.
 *
 * testCacheHit():
 *
 * Creates a mock TestObject with a method getName().
 *
 * Calls getObjectByPath twice with the same propertyPath and object.
 *
 * Asserts that getName() is called only once which shows the cache is hit for the second call and that the cache is working as intended.
 *
 * Verifies the result is correct.
 *
 * testCacheWithDifferentInstances():
 *
 * Creates two mock objects with different instances and the same property path.
 *
 * Calls getObjectByPath twice with different object instances and the same propertyPath.
 *
 * Verifies the result is correct and the method is called on both objects once which means the cache has different results for different instances.
 *
 * testCacheClearBetweenEvaluations():
 *
 * Creates a mock object and calls getObjectByPath after a rule evaluation and after another rule evaluation.
 *
 * Verify that the mock method is called twice which means that the cache is cleared between the evaluations.
 *
 * testMapPropertyAccess():
 *
 * Creates a map and test if map access is cached correctly.
 *
 * Verifies that the get method is only called once for the map and cached for the same map and property.
 *
 * TestObject: A simple class used for testing the object method invocation by mock.
 *
 * Key points in the test:
 *
 * Mockito: Used for mocking the object and verify the method invocation count.
 *
 * Explicit verification: Mockito's verify(mockObject, times(1)).method() helps confirm the method is invoked once, implying a cache hit for second call.
 *
 * Testing with different object instances: Helps confirm that the cache is not mixing up results for different objects.
 *
 * Testing cache clearing: Ensures that stale cache data is not used in the second evaluation.
 *
 * Comprehensive tests: Ensure that both the POJO object method invocation and the map object get method is covered.
 */
import com.networknt.rule.Rule;
import com.networknt.rule.RuleCondition;
import com.networknt.rule.RuleConditionValue;
import com.networknt.rule.RuleEvaluator;
import com.networknt.rule.exception.RuleEngineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@Disabled
public class RuleEvaluatorCacheTest {

    private RuleEvaluator ruleEvaluator;
    private Rule rule;
    private RuleCondition condition;
    private RuleConditionValue conditionValue;
    private String ruleId;
    private String conditionId;

    @BeforeEach
    void setup() {
        ruleEvaluator = RuleEvaluator.getInstance();
        ruleId = "test-rule";
        conditionId = "test-condition";
        rule = new Rule();
        rule.setRuleId(ruleId);
        condition = new RuleCondition();
        condition.setConditionId(conditionId);
        conditionValue = new RuleConditionValue();
    }

    @Test
    void testCacheHit() throws Exception {
        // Create a mock object
        TestObject testObject = Mockito.mock(TestObject.class);
        when(testObject.getName()).thenReturn("test-name");

        // getObjectByPath will be called twice, but only the first time, the mock method
        // will be called, and the second time, the cache will be hit.
        Object result1 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject);
        Object result2 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject);

        assertEquals("test-name", result1);
        assertEquals("test-name", result2);

        verify(testObject, times(1)).getName();
    }

    @Test
    void testCacheWithDifferentInstances() throws Exception {
        // Create two mock objects with different instances.
        TestObject testObject1 = Mockito.mock(TestObject.class);
        when(testObject1.getName()).thenReturn("test-name1");
        TestObject testObject2 = Mockito.mock(TestObject.class);
        when(testObject2.getName()).thenReturn("test-name2");


        // getObjectByPath will be called twice with two different instances, the mock method
        // will be called twice
        Object result1 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject1);
        Object result2 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject2);

        assertEquals("test-name1", result1);
        assertEquals("test-name2", result2);

        verify(testObject1, times(1)).getName();
        verify(testObject2, times(1)).getName();
    }
    @Test
    void testCacheClearBetweenEvaluations() throws Exception {
        // Create a mock object
        TestObject testObject = Mockito.mock(TestObject.class);
        when(testObject.getName()).thenReturn("test-name");


        // First Evaluation
        ruleEvaluator.evaluate(rule, new HashMap<>(), new HashMap<>());
        Object result1 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject);


        // Second Evaluation. The cache should be cleared after the first evaluation, so it will call the mock method again
        ruleEvaluator.evaluate(rule, new HashMap<>(), new HashMap<>());
        Object result2 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Name", testObject);


        assertEquals("test-name", result1);
        assertEquals("test-name", result2);

        verify(testObject, times(2)).getName();
    }

    @Test
    void testMapPropertyAccess() throws Exception {
        Map<String, Object> mapObject = Mockito.mock(Map.class);
        when(mapObject.get("Prop1")).thenReturn("value1");

        // getObjectByPath will be called twice with two different instances, the mock method
        // will be called only once for the map
        Object result1 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Prop1", mapObject);
        Object result2 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "Prop1", mapObject);

        assertEquals("value1", result1);
        assertEquals("value1", result2);

        verify(mapObject, times(1)).get("Prop1");
    }
    @Test
    void testNestedProperty() throws Exception {
        // Create a mock object
        TestObject testObject = Mockito.mock(TestObject.class);
        SubTestObject subTestObject = Mockito.mock(SubTestObject.class);
        when(testObject.getSubTestObject()).thenReturn(subTestObject);
        when(subTestObject.getProp1()).thenReturn("nestedValue");

        // getObjectByPath will be called twice, but only the first time, the mock method
        // will be called, and the second time, the cache will be hit.
        Object result1 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "SubTestObject.Prop1", testObject);
        Object result2 = ruleEvaluator.getObjectByPath(ruleId, conditionId, "SubTestObject.Prop1", testObject);


        assertEquals("nestedValue", result1);
        assertEquals("nestedValue", result2);

        verify(testObject, times(1)).getSubTestObject();
        verify(subTestObject, times(1)).getProp1();

    }
    static class TestObject {
        String name;
        SubTestObject subTestObject;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public SubTestObject getSubTestObject() {
            return this.subTestObject;
        }

        public void setSubTestObject(SubTestObject subTestObject) {
            this.subTestObject = subTestObject;
        }
    }
    static class SubTestObject {
        String prop1;

        public String getProp1() {
            return prop1;
        }
        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }
    }
}