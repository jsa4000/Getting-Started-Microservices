package com.example.kafkaconsumer.event.role;

import com.example.kafkaconsumer.event.base.Message;
import com.example.kafkaconsumer.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class RoleCreated extends Message {

    @Getter private Role role;

    public RoleCreated (Role role) {
        super(role.getId());
        this.role = role;
    }
}
