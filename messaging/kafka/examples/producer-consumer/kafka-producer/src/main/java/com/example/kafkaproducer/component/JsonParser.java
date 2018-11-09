package com.example.kafkaproducer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonParser {

    public static String serialize(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException ex) {
            log.error(ex.getMessage(),ex);
        }
        return null;
    }

    public static <T> T deserialize(String json, Class<T> type) {
        try {
            return new ObjectMapper().readValue(json,type);
        }
        catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        }
        return null;
    }

    public static <T> T deserialize(String json, Class<?> type, Class<?> genericType) {
        try {
            ObjectMapper mapper = new  ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(type, genericType);
            return mapper.readValue(json, javaType);

        }
        catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        }
        return null;
    }
}
