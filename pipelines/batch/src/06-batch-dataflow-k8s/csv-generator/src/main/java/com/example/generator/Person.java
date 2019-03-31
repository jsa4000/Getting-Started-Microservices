package com.example.generator;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
public class Person implements Rower {

    private String id;

    private Name name;

    private String gender;

    private String email;

    private String phone;

    private Date birth;

    private Address address;

    private String company;

    private String creditCardNumber;

    private String jobTitle;

    private Date startDate;

    private Date endDate;

    public Person() {
        Faker faker = new Faker();
        id = faker.idNumber().valid();
        name = faker.name();
        gender = faker.name().title();
        email = faker.internet().emailAddress();
        phone = faker.phoneNumber().cellPhone();
        birth = faker.date().birthday(0,65);
        address = faker.address();
        company = faker.company().name();
        creditCardNumber = faker.idNumber().ssnValid();
        jobTitle = faker.job().title();
        startDate = faker.date().past(1000, TimeUnit.DAYS);
        endDate = faker.date().past(25, TimeUnit.HOURS);
    }

    public String[] row() {
        return new String[]{
                id,
                name.firstName(),
                name.lastName(),
                name.fullName(),
                gender,
                email,
                phone,
                birth.toString(),
                address.fullAddress(),
                address.streetName(),
                address.cityName(),
                address.zipCode(),
                address.country(),
                address.state(),
                company,
                creditCardNumber,
                jobTitle,
                startDate.toString(),
                endDate.toString()
        };
    }
}
