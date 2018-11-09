package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.EventBase;
import com.example.kafkaproducer.event.enums.UserEvent;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UserDeleted extends EventBase {

    private final String VERSION = "1.0.0";

    @Getter
    private String id;

    public UserDeleted (String id) {
        this.id = id;
        this.version = VERSION;
        this.type = UserEvent.DELETED.getName();
    }
}
