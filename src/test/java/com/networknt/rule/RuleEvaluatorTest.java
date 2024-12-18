package com.networknt.rule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class RuleEvaluatorTest {
    @Test
    public void testEvaluateConditionExpression() throws Exception {
        RuleEvaluator ruleEvaluator = RuleEvaluator.getInstance();
        Collection<RuleCondition> conditions = new ArrayList<>();
        RuleCondition ruleCondition1 = mock(RuleCondition.class);
        when(ruleCondition1.getConditionId()).thenReturn("cid1");
        when(ruleCondition1.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition1.getOperatorCode()).thenReturn("equals");

        RuleCondition ruleCondition2 = mock(RuleCondition.class);
        when(ruleCondition2.getConditionId()).thenReturn("cid2");
        when(ruleCondition2.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition2.getOperatorCode()).thenReturn("equals");

        RuleCondition ruleCondition3 = mock(RuleCondition.class);
        when(ruleCondition3.getConditionId()).thenReturn("cid3");
        when(ruleCondition3.getPropertyPath()).thenReturn("propertyPath");
        when(ruleCondition3.getOperatorCode()).thenReturn("equals");


        conditions.add(ruleCondition1);
        conditions.add(ruleCondition2);
        conditions.add(ruleCondition3);
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> objMap = new HashMap<>();

        String expression = "(cid1 OR (cid2 AND cid3))";
        boolean result = ruleEvaluator.evaluateConditionExpression("test-rule",expression, conditions, objMap, resultMap);

        Assertions.assertTrue(result);

        // verify all the methods were called.
        verify(ruleCondition1, times(4)).getConditionId();
        verify(ruleCondition2, times(3)).getConditionId();
        verify(ruleCondition3, times(2)).getConditionId();

    }
}
