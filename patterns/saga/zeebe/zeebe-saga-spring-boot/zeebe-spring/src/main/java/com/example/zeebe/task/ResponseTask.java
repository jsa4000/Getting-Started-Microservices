package com.example.zeebe.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTask<T>{

    static public final  String RESPONSE_SUCCESS_KEY = "success";
    static public final  String RESPONSE_DATA_KEY = "data";
    static public final  String RESPONSE_ERROR_KEY = "error";

    Boolean success;
    T data;
    TaskError error;

    @SuppressWarnings("unchecked")
    static public <T> ResponseTask<T> ok() {
        return (ResponseTask) ResponseTask.builder().success(true).build();
    }

    @SuppressWarnings("unchecked")
    static public <T> ResponseTask<T> ok(T data) {
        return (ResponseTask) ResponseTask.builder().success(true).data(data).build();
    }

    @SuppressWarnings("unchecked")
    static public <T> ResponseTask<T> ko(TaskError error) {
        return (ResponseTask) ResponseTask.builder().success(false).error(error).build();
    }

    public Map<String, Object> serialize(ObjectMapper objectMapper) throws JsonProcessingException {
        return Map.of(RESPONSE_SUCCESS_KEY, success,
                RESPONSE_DATA_KEY, objectMapper.writeValueAsString(data),
                RESPONSE_ERROR_KEY, objectMapper.writeValueAsString(error));
    }

    @SuppressWarnings("unchecked")
    static public <T> ResponseTask<T> deserialize(Object object, ObjectMapper objectMapper, Class<T> valueType) throws Throwable {
        if (!(object instanceof Map))
            throw new IOException("Error reading object from response");
        Map response = (Map) object;
        return (ResponseTask<T>) ResponseTask.builder()
                .success(Boolean.parseBoolean(response.get(RESPONSE_SUCCESS_KEY).toString()))
                .data(objectMapper.readValue(response.get(RESPONSE_DATA_KEY).toString(), valueType))
                .error(objectMapper.readValue(response.get(RESPONSE_ERROR_KEY).toString(), TaskError.class))
                .build();
    }

}
