package com.example.kafkaconsumer.event.user;

import com.example.kafkaconsumer.event.base.Message;
import com.example.kafkaconsumer.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserModified extends Message {

    @Getter private User user;

    public UserModified (User user) {
        super(user.getId());
        this.user = user;
    }
}
