package com.example.kafkaconsumer.event.user;

import com.example.kafkaconsumer.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserCreated {

    public static final String VERSION = "1.0.0";

    @Getter
    private User user;

}
