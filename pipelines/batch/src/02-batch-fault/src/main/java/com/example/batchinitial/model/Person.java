package com.example.batchinitial.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private Date updateTime;

    public Person(String firstName, String lastName, Date updateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updateTime = updateTime;
    }
}
