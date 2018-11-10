package com.example.kafkaproducer.event.user;

import com.example.kafkaproducer.event.base.Message;
import com.example.kafkaproducer.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserCreated  extends Message {

    @Getter private User user;

    public UserCreated (User user) {
        super(user.getId());
        this.user = user;
    }
}
