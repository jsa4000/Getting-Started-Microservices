package com.example.process.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String title;
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
    private int department;
    private Date startDate;
    private Date endDate;

    // Enriched Properties
    private String groupName;
    private Date updateTime;

}
