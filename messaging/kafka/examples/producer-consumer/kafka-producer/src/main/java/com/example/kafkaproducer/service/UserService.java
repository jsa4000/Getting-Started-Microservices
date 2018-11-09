package com.example.kafkaproducer.service;

import com.example.kafkaproducer.event.base.Event;
import com.example.kafkaproducer.event.user.UserCreated;
import com.example.kafkaproducer.event.user.UserDeleted;
import com.example.kafkaproducer.event.user.UserModified;
import com.example.kafkaproducer.model.User;
import com.example.kafkaproducer.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    public String createUser(User user) {
        Event event = new Event<>(new UserCreated(user));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        return event.getId();
    }

    public String modifyUser(User user) {
        Event event = new Event<>(new UserModified(user));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        return event.getId();
    }

    public String deleteUser(String userId) {
        Event event = new Event<>(new UserDeleted(userId));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        return event.getId();
    }
}
