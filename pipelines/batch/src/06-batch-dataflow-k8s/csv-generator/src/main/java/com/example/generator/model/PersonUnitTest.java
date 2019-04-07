package com.example.generator.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PersonUnitTest {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected String title;
    protected String email;
    protected String phone;
    protected Date birth;
    protected String address;
    protected String street;
    protected String city;
    protected String country;
    protected String state;
    protected String zipCode;
    protected String company;
    protected String creditCardNumber;
    protected String jobTitle;
    protected Date startDate;
    protected Date endDate;

    @Override
    public String toString() { return "PersonUnitTest{" + "id='" + id + '}'; }
}

