package com.example.kafkaproducer.service;

import com.example.kafkaproducer.component.KafkaProducer;
import com.example.kafkaproducer.event.base.Event;
import com.example.kafkaproducer.event.user.UserCreated;
import com.example.kafkaproducer.event.user.UserDeleted;
import com.example.kafkaproducer.event.user.UserModified;
import com.example.kafkaproducer.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Value("${application.topic.users}")
    private String usersTopic;

    @Autowired
    private KafkaProducer producer;

    public String createUser(User user) {
        Event event = new Event<>(new UserCreated(user));
        log.debug(event.toString());
        producer.publish(event, usersTopic);
        return event.getId();
    }

    public String modifyUser(User user) {
        Event event = new Event<>(new UserModified(user));
        log.debug(event.toString());
        producer.publish(event, usersTopic);
        return event.getId();
    }

    public String deleteUser(String userId) {
        Event event = new Event<>(new UserDeleted(userId));
        log.debug(event.toString());
        producer.publish(event, usersTopic);
        return event.getId();
    }
}
