package com.example.kafkaconsumer.event.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserDeleted {

    public static final String VERSION = "1.0.0";

    @Getter
    private String id;

}
