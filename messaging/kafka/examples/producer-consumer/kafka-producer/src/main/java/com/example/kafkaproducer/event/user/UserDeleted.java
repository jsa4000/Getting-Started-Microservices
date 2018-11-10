package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserDeleted extends Message {

    @Getter private String id;

    public UserDeleted (String id) {
        super(id);
        this.id = id;
    }
}
