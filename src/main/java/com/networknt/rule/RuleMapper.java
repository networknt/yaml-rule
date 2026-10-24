package com.networknt.rule;

import tools.jackson.core.type.TypeReference;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.util.Collection;
import java.util.Map;

public class RuleMapper {
    public static ObjectMapper yamlMapper = YAMLMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                    DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
            .build();

    public static Map<String, Rule> string2RuleMap(String s) {
        try {
            return yamlMapper.readValue(s, new TypeReference<Map<String, Rule>>(){});
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Collection<Rule>> string2GroupMap(String s) {
        try {
            return yamlMapper.readValue(s, new TypeReference<Map<String, Collection<Rule>>>(){});
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

}
