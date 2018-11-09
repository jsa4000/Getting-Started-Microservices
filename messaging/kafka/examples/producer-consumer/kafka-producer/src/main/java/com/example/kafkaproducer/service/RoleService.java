package com.example.kafkaproducer.service;

import com.example.kafkaproducer.event.base.Event;
import com.example.kafkaproducer.event.role.RoleCreated;
import com.example.kafkaproducer.event.role.RoleDeleted;
import com.example.kafkaproducer.event.role.RoleModified;
import com.example.kafkaproducer.model.Role;
import com.example.kafkaproducer.utils.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleService {

    public String createRole(Role role) {
        Event event = new Event<>(new RoleCreated(role));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        Event dEvent = JsonParser.deserialize(JsonParser.serialize(event),Event.class);

        return event.getId();
    }

    public String modifyRole(Role role) {
        Event event = new Event<>(new RoleModified(role));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        return event.getId();
    }

    public String deleteRole(String roleId) {
        Event event = new Event<>(new RoleDeleted(roleId));

        log.info(event.toString());
        log.info(JsonParser.serialize(event));

        return event.getId();
    }

}
