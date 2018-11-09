package com.example.kafkaproducer.service;

import com.example.kafkaproducer.component.KafkaProducer;
import com.example.kafkaproducer.event.base.Event;
import com.example.kafkaproducer.event.role.RoleCreated;
import com.example.kafkaproducer.event.role.RoleDeleted;
import com.example.kafkaproducer.event.role.RoleModified;
import com.example.kafkaproducer.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleService {

    @Value("${application.topic.roles}")
    private String rolesTopic;

    @Autowired
    private KafkaProducer producer;

    public String createRole(Role role) {
        Event event = new Event<>(new RoleCreated(role));
        log.debug(event.toString());
        producer.publish(event, rolesTopic);
        return event.getId();
    }

    public String modifyRole(Role role) {
        Event event = new Event<>(new RoleModified(role));
        log.debug(event.toString());
        producer.publish(event, rolesTopic);
        return event.getId();
    }

    public String deleteRole(String roleId) {
        Event event = new Event<>(new RoleDeleted(roleId));
        log.debug(event.toString());
        producer.publish(event, rolesTopic);
        return event.getId();
    }



}
