package com.example.zeebe.task;

import com.example.zeebe.autoconfigure.ZeebeObjectMapperAutoConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.example.zeebe.task.ResponseTask.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ZeebeObjectMapperAutoConfiguration.class)
class ResponseTaskTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void serialize_ok_mustReturnOk() throws JsonProcessingException {
        ResponseTask<Booking> response = ResponseTask.ok(getBooking());

        Map<String,Object> expected = Map.of(RESPONSE_SUCCESS_KEY, response.getSuccess(),
                RESPONSE_DATA_KEY, objectMapper.writeValueAsString(response.getData()),
                RESPONSE_ERROR_KEY, objectMapper.writeValueAsString(response.getError()));

        Map<String,Object> result = response.serialize(objectMapper);

        Assert.notNull(result,"Serialized object must not be null");
        Assert.isTrue(result.get(RESPONSE_SUCCESS_KEY).equals(expected.get(RESPONSE_SUCCESS_KEY)), "Item must be equal");
        Assert.isTrue(result.get(RESPONSE_ERROR_KEY).equals(expected.get(RESPONSE_ERROR_KEY)), "Item must be equal");
    }

    @Test
    void serialize_ko_mustReturnOk() throws JsonProcessingException {
        ResponseTask<Booking> response = ResponseTask.ko(TaskError.INPUT_ERROR);

        Map<String,Object> expected = Map.of(RESPONSE_SUCCESS_KEY, response.getSuccess(),
                RESPONSE_DATA_KEY, objectMapper.writeValueAsString(response.getData()),
                RESPONSE_ERROR_KEY, objectMapper.writeValueAsString(response.getError()));

        Map<String,Object> result = response.serialize(objectMapper);

        Assert.notNull(result,"Serialized object must not be null");
        Assert.isTrue(result.get(RESPONSE_SUCCESS_KEY) == expected.get(RESPONSE_SUCCESS_KEY), "Item must be equal");
        Assert.isTrue(result.get(RESPONSE_DATA_KEY).equals(expected.get(RESPONSE_DATA_KEY)), "Item must be equal");
        Assert.isTrue(result.get(RESPONSE_ERROR_KEY).equals(expected.get(RESPONSE_ERROR_KEY)), "Item must be equal");
    }

    @Test
    void deserialize_ok_mustReturnOk() throws Throwable {
        ResponseTask<Booking> expected = ResponseTask.ok(getBooking());
        Map<String,Object> values = Map.of(RESPONSE_SUCCESS_KEY, expected.getSuccess(),
                RESPONSE_DATA_KEY, objectMapper.writeValueAsString(expected.getData()),
                RESPONSE_ERROR_KEY, objectMapper.writeValueAsString(expected.getError()));

        ResponseTask<Booking> result = ResponseTask.deserialize(values, objectMapper, Booking.class);

        Assert.notNull(result,"Serialized object must not be null");
        Assert.isTrue(result.success == expected.success, "Item must be equal");
        Assert.isTrue(result.data.id.equals(expected.data.id), "Item must be equal");
        Assert.isNull(result.error,  "Item must be null");
    }

    @Test
    void deserialize_ko_mustReturnOk() throws Throwable {
        ResponseTask<Booking> expected = ResponseTask.ko(TaskError.INPUT_ERROR);
        Map<String,Object> values = Map.of(RESPONSE_SUCCESS_KEY, expected.getSuccess(),
                RESPONSE_DATA_KEY, objectMapper.writeValueAsString(expected.getData()),
                RESPONSE_ERROR_KEY, objectMapper.writeValueAsString(expected.getError()));

        ResponseTask<Booking> result = ResponseTask.deserialize(values, objectMapper, Booking.class);

        Assert.notNull(result,"Serialized object must not be null");
        Assert.isTrue(result.success == expected.success, "Item must be equal");
        Assert.isNull(result.data, "Item must be null");
        Assert.isTrue(result.error.toString().equals(expected.error.toString()), "Item must be equal");
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class Booking {

        String id;
        String clientId;
        String resourceId;
        OffsetDateTime fromDate;
        OffsetDateTime toDate;
        OffsetDateTime createdAt;
        Boolean active;

    }

    private Booking getBooking() {
        return Booking.builder()
                .id("123")
                .clientId("456")
                .resourceId("789")
                .fromDate(OffsetDateTime.now())
                .toDate(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .active(true)
                .build();
    }

}