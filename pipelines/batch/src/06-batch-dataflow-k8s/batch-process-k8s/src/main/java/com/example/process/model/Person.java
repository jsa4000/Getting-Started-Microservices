package com.example.process.model;

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
    private String groupName;
    private Date updateTime;

    public Person(String firstName, String lastName, String department, String groupName, Date updateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.groupName = groupName;
        this.updateTime = updateTime;
    }
}
