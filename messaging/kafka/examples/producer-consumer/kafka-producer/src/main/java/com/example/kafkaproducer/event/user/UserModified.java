package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.Message;
import com.example.kafkaproducer.event.enums.UserEvent;
import com.example.kafkaproducer.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserModified extends Message {

    private static final String VERSION = "1.0.0";

    @Getter
    private User user;

    public UserModified (User user) {
        super(user.getId(), VERSION, UserEvent.MODIFIED.getName());
        this.user = user;
    }
}
