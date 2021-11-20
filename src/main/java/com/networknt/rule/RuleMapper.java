package com.networknt.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class RuleMapper {
    public static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static Map<String, Rule> string2RuleMap(String s) {
        try {
            return yamlMapper.readValue(s, new TypeReference<Map<String, Rule>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Collection<Rule>> string2GroupMap(String s) {
        try {
            return yamlMapper.readValue(s, new TypeReference<Map<String, Collection<Rule>>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
