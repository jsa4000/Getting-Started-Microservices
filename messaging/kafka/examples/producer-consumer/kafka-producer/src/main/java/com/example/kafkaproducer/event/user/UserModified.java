package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.EventBase;
import com.example.kafkaproducer.event.enums.UserEvent;
import com.example.kafkaproducer.model.User;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UserModified extends EventBase {

    private final String VERSION = "1.0.0";

    @Getter
    private User user;

    public UserModified (User user) {
        this.user = user;
        this.version = VERSION;
        this.type = UserEvent.MODIFIED.getName();
    }
}
