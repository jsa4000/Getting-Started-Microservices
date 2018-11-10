package com.example.kafkaproducer.event.role;

import com.example.kafkaproducer.event.base.Message;
import com.example.kafkaproducer.event.enums.RoleEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class RoleDeleted extends Message {

    private static final String VERSION = "1.0.0";

    @Getter
    private String id;

    public RoleDeleted (String id) {
        super(id, VERSION, RoleEvent.DELETED.getName());
        this.id = id;
    }
}
