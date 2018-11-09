package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.EventBase;
import com.example.kafkaproducer.event.enums.UserEvent;
import com.example.kafkaproducer.model.User;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UserCreated  extends EventBase {

    private final String VERSION = "1.0.0";

    @Getter
    private User user;

    public UserCreated (User user) {
        this.user = user;
        this.version = VERSION;
        this.type = UserEvent.CREATED.getName();
    }
}
