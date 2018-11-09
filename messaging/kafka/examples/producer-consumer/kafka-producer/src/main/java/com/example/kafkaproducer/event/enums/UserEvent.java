package com.example.kafkaproducer.event.enums;

import lombok.Getter;

public enum UserEvent {
    CREATED ("USER_CREATED"), MODIFIED("USER_MODIFIED"), DELETED("USER_DELETED");

    @Getter
    private String name;
    UserEvent(String name) { this.name = name; }
}
