package com.example.kafkaproducer.event.role;

import com.example.kafkaproducer.event.base.EventBase;
import com.example.kafkaproducer.event.enums.RoleEvent;
import lombok.Getter;
import lombok.ToString;

@ToString
public class RoleDeleted extends EventBase {

    private final String VERSION = "1.0.0";

    @Getter
    private String id;

    public RoleDeleted (String id) {
        this.id = id;
        this.version = VERSION;
        this.type = RoleEvent.DELETED.getName();
    }
}
