package com.example.batch.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="Person")
@NoArgsConstructor
public class Person {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "updateTime")
    private Date updateTime;

    public Person(String firstName, String lastName, Date updateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updateTime = updateTime;
    }
}
