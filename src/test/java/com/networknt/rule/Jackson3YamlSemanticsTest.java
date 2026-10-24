package com.networknt.rule;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.*;

class Jackson3YamlSemanticsTest {

    @Test
    void preservesYaml12OperatorAndStringScalars() {
        for (String scalar : new String[]{"yes", "no", "on", "off", "012", "0b11", "1_000", "~"}) {
            var rules = RuleMapper.string2RuleMap("r:\n  ruleId: " + scalar
                    + "\n  conditions:\n    - operatorCode: " + scalar + "\n");
            assertEquals(scalar, rules.get("r").getRuleId());
            assertEquals(scalar, rules.get("r").getConditions().iterator().next().getOperatorCode());
        }
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
