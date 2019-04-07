package com.example.generator.model;

import com.example.generator.writer.Rower;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerTest implements Rower {

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

    private int department;

    private Date startDate;

    private Date endDate;

    public CustomerTest() {
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
        department = 1;
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
                String.valueOf(department),
                startDate.toString(),
                endDate.toString()
        };
    }
}
