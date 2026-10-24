package com.networknt.rule;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.*;

class Jackson3YamlSemanticsTest {

    @Test
    void preservesYaml12OperatorAndStringScalars() {
        for (String scalar : new String[]{"yes", "no", "on", "off", "012", "0b11", "1_000", "~",
                "True", "TRUE", "False", "FALSE", "Null", "NULL", "0x1F"}) {
            var rules = RuleMapper.string2RuleMap("r:\n  ruleId: " + scalar
                    + "\n  conditions:\n    - operatorCode: " + scalar + "\n");
            assertEquals(scalar, rules.get("r").getRuleId());
            assertEquals(scalar, rules.get("r").getConditions().iterator().next().getOperatorCode());
        }
    }

    @Test
    void booleanAndIntegerFieldsExposeTheYamlScalarBreak() {
        for (String scalar : new String[]{"yes", "no", "on", "off", "~", "Null", "NULL"}) {
            assertInstanceOf(JacksonException.class, assertThrows(RuntimeException.class, () -> RuleMapper.string2RuleMap(
                    "r:\n  actions:\n    - conditionResult: " + scalar + "\n")).getCause(), scalar);
        }
        assertInstanceOf(JacksonException.class, assertThrows(RuntimeException.class, () -> RuleMapper.string2RuleMap(
                "r:\n  conditions:\n    - index: 0x1\n")).getCause());
        var rule = RuleMapper.string2RuleMap("r:\n  conditions:\n    - index: 010\n"
                + "  actions:\n    - conditionResult: null\n    - conditionResult: true\n").get("r");
        assertEquals(10, rule.getConditions().iterator().next().getIndex());
        var actions = rule.getActions().iterator();
        assertNull(actions.next().isConditionResult());
        assertEquals(Boolean.TRUE, actions.next().isConditionResult());
    }

    @Test
    void collectionFieldsRejectFormerNullAliasesAndUntypedValuesRemainStrings() {
        for (String scalar : new String[]{"~", "Null", "NULL"}) {
            assertInstanceOf(JacksonException.class, assertThrows(RuntimeException.class, () -> RuleMapper.string2RuleMap(
                    "r:\n  conditions: " + scalar + "\n")).getCause());
        }
        var rule = RuleMapper.string2RuleMap("r:\n  conditions: []\n  actions:\n"
                + "    - parameters:\n        values: [yes, True, FALSE, Null, NULL, 0x1F, 010, null, true, false]\n").get("r");
        assertTrue(rule.getConditions().isEmpty());
        assertEquals(java.util.Arrays.asList("yes", "True", "FALSE", "Null", "NULL", "0x1F", "010", null, true, false),
                rule.getActions().iterator().next().getParameters().get("values"));
    }

    @Test
    void preservesExistingErrorWrappingAndUnknownPropertyRejection() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> RuleMapper.string2RuleMap("r:\n  unknownField: true\n"));
        assertInstanceOf(UnrecognizedPropertyException.class, exception.getCause());
        assertNotNull(assertThrows(RuntimeException.class,
                () -> RuleMapper.string2RuleMap("r: [unterminated")).getCause());
        assertNotNull(assertThrows(RuntimeException.class,
                () -> RuleMapper.string2GroupMap("g: [unterminated")).getCause());
    }

    @Test
    void preservesFirstDocumentBehavior() {
        var rules = RuleMapper.string2RuleMap("first:\n  ruleId: first\n---\nsecond:\n  ruleId: second\n");
        assertEquals(1, rules.size());
        assertEquals("first", rules.get("first").getRuleId());
    }
}
