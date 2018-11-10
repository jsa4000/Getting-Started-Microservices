package com.example.kafkaconsumer;

import com.example.kafkaconsumer.component.JsonParser;
import com.example.kafkaconsumer.event.base.Event;
import com.example.kafkaconsumer.event.role.RoleCreated;
import com.example.kafkaconsumer.model.Role;
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

        Event<RoleCreated> event = JsonParser
                .deserialize(JsonParser.serialize(expectedEvent),Event.class, RoleCreated.class);

        assertTrue(event.getId().equals(expectedEvent.getId()));
        assertTrue(event.getTimestamp() == expectedEvent.getTimestamp());
        assertTrue(event.getMessageType().equals(expectedEvent.getMessageType()));
        assertTrue(event.getMessageVersion().equals(expectedEvent.getMessageVersion()));
        assertTrue(event.getMessage().getRole().getId().equals(expectedRole.getId()));
        assertTrue(event.getMessage().getRole().getName().equals(expectedRole.getName()));
    }

    @Test
    public void getValue_EventRoleCreated_shouldReturnOk() {
        Role expectedRole = getDefaultRole();
        Event expectedEvent = new Event<>(new RoleCreated(expectedRole));

        String json = JsonParser.serialize(expectedEvent);
        String version = JsonParser.getValue( "messageVersion", json);
        String type = JsonParser.getValue("messageType", json);

        assertTrue(expectedEvent.getMessageType().equals(type));
        assertTrue(expectedEvent.getMessageVersion().equals(version));
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