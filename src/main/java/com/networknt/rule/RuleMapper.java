package com.networknt.rule;

import java.util.Collection;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class RuleMapper {
    public static ObjectMapper yamlMapper = YAMLMapper.builder()
            .configureForJackson2()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES)
            .enable(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)
            .disable(StreamReadFeature.USE_FAST_DOUBLE_PARSER, StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER)
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
