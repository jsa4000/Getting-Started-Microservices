package com.example.generator.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PersonBuilder{

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private String email;
    private String phone;
    private Date birth;
    private String address;
    private String street;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String company;
    private String creditCardNumber;
    private String jobTitle;
    private Date startDate;
    private Date endDate;

    @Override
    public String toString() {
        return "PersonUnitTest{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName +
                '}';
    }
}
