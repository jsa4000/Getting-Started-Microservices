package com.example.kafkaproducer.event.role;

import com.example.kafkaproducer.event.base.EventBase;
import com.example.kafkaproducer.event.enums.RoleEvent;
import com.example.kafkaproducer.model.Role;
import lombok.Getter;
import lombok.ToString;

@ToString
public class RoleCreated extends EventBase {

    private final String VERSION = "1.0.0";

    @Getter
    private Role role;

    public RoleCreated (Role role) {
        this.role = role;
        this.version = VERSION;
        this.type = RoleEvent.CREATED.getName();
    }
}
