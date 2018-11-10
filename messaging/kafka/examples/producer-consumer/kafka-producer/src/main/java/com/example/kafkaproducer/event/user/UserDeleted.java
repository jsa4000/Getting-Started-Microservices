package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.Message;
import com.example.kafkaproducer.event.enums.UserEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserDeleted extends Message {

    private static final String VERSION = "1.0.0";

    @Getter
    private String id;

    public UserDeleted (String id) {
        super(id, VERSION, UserEvent.DELETED.getName());
        this.id = id;
    }
}
