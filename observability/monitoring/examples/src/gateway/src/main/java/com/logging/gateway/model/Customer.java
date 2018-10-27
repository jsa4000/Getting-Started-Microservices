package com.logging.gateway.model;

import lombok.*;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Customer {

    @Getter
    private long id;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private int age;
}
