package com.example.kafkaproducer.event.role;

import com.example.kafkaproducer.event.base.Message;
import com.example.kafkaproducer.event.enums.RoleEvent;
import com.example.kafkaproducer.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class RoleModified extends Message {

    private static final String VERSION = "1.0.0";

    @Getter
    private Role role;

    public RoleModified (Role role) {
        super(role.getId(), VERSION, RoleEvent.MODIFIED.getName());
        this.role = role;
    }
}
