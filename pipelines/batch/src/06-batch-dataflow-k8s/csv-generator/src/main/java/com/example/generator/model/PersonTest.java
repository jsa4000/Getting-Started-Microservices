package com.example.generator.model;

import com.example.generator.writer.Rower;
import lombok.Data;

import java.util.Date;

@Data
public class PersonTest implements Rower {

    private String id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String title;

    private String fullAddress;

    private String streetName;

    private String cityName;

    private String zipCode;

    private String country;

    private String state;

    private String email;

    private String phone;

    private Date birth;

    private String company;

    private String creditCardNumber;

    private String jobTitle;

    private Date startDate;

    private Date endDate;

    public PersonTest() {
        id = "Sample";
        firstName= "firstName";
        lastName= "lastName";
        fullName= "fullName";
        title = "title";
        fullAddress = "fullAddress";
        streetName = "streetName";
        cityName = "cityName";
        zipCode = "zipCode";
        country = "country";
        state = "state";
        email = "email";
        phone = "phone";
        birth = new Date();
        company = "company";
        creditCardNumber = "creditCardNumber";
        jobTitle = "jobTitle";
        startDate = new Date();
        endDate = new Date();

    }

    public String[] row() {
        return new String[]{
                id,
                firstName,
                lastName,
                fullName,
                title,
                email,
                phone,
                birth.toString(),
                fullAddress ,
                streetName,
                cityName,
                zipCode ,
                country ,
                state,
                company,
                creditCardNumber,
                jobTitle,
                startDate.toString(),
                endDate.toString()
        };
    }
}
