package com.example.kafkaproducer.component;

import com.example.kafkaproducer.event.base.Event;
import com.example.kafkaproducer.event.role.RoleCreated;
import com.example.kafkaproducer.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JsonParserTest {

    @Test
    public void serialize_JsonRole_shouldReturnOk() {
        Role role = getDefaultRole();
        String expectedJsonRole = getJsonRole(getDefaultRole());

        String jsonRole = JsonParser.serialize(role);

        assertTrue(jsonRole.equals(expectedJsonRole));
    }

    @Test
    public void deserialize_RoleClass_shouldReturnOk() {
        String jsonRole = getJsonRole(getDefaultRole());
        Role expectedRole = getDefaultRole();

        Role role = JsonParser.deserialize(jsonRole,Role.class);

        assertTrue(role.getId().equals(expectedRole.getId()));
        assertTrue(role.getName().equals(expectedRole.getName()));
    }

    @Test
    public void deserialize_EventRoleCreatedClass_shouldReturnOk() {
        Role expectedRole = getDefaultRole();
        Event expectedEvent = new Event<>(new RoleCreated(expectedRole));

        Event event = JsonParser.deserialize(JsonParser.serialize(expectedEvent),Event.class, RoleCreated.class);

        assertTrue(event.getId().equals(expectedEvent.getId()));
        assertTrue(event.getTimestamp() == expectedEvent.getTimestamp());
        assertTrue(event.getMessageType().equals(expectedEvent.getMessageType()));
        assertTrue(event.getMessageVersion().equals(expectedEvent.getMessageVersion()));
        assertTrue(((RoleCreated) event.getMessage()).getRole().getId().equals(expectedRole.getId()));
        assertTrue(((RoleCreated) event.getMessage()).getRole().getName().equals(expectedRole.getName()));
    }

    private Role getDefaultRole() {
        return new Role ("ROLE_ADMIN", "ROLE_ADMIN");
    }

    private String getJsonRole(Role role) {
        return  new ObjectMapper().createObjectNode()
                .put("id", role.getId())
                .put("name",  role.getName())
                .toString();
    }
}