package com.example.kafkaconsumer.event.role;

import com.example.kafkaconsumer.event.base.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class RoleDeleted extends Message {

    @Getter private String id;

    public RoleDeleted (String id) {
        super(id);
        this.id = id;
    }
}
