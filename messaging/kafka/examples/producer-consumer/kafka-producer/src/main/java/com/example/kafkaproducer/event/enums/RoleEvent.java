package com.example.kafkaproducer.event.enums;

import lombok.Getter;

public enum RoleEvent {
    CREATED ("ROLE_CREATED"), MODIFIED("ROLE_MODIFIED"), DELETED("ROLE_DELETED");

    @Getter
    private String name;
    RoleEvent(String name) { this.name = name; }
}
