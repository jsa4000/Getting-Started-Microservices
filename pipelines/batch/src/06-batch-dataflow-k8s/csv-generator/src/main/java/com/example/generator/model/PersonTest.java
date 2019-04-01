package com.example.generator.model;

import com.example.generator.writer.Rower;
import lombok.Data;

import java.util.Date;

@Data
public class PersonTest implements Rower {

    private String id;

    private String name;

    private String gender;

    private String address;

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
        name = "NAme";
        gender = "Sample";
        address = "Sample";
        email = "Sample";
        phone = "Sample";
        birth = new Date();
        company = "Sample";
        creditCardNumber = "Sample";
        jobTitle = "Sample";
        startDate = new Date();
        endDate = new Date();

    }

    public String[] row() {
        return new String[]{
                id,
                name,
                gender,
                email,
                phone,
                birth.toString(),
                address,
                company,
                creditCardNumber,
                jobTitle,
                startDate.toString(),
                endDate.toString()
        };
    }
}
