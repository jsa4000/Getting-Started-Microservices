package com.example.batch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Person  {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer departmentId;
    private String department;
    private Date updateTime;

    public Person(String firstName, String lastName, String department, Date updateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.updateTime = updateTime;
    }
}
