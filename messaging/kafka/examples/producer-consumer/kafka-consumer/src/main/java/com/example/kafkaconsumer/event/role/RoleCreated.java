package com.example.kafkaconsumer.event.role;

import com.example.kafkaconsumer.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class RoleCreated {

    public static final String VERSION = "1.0.0";

    @Getter
    private Role role;
}
