package com.example.generator.model;

import com.example.generator.writer.Rower;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
public class Person implements Rower {

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    static private Faker faker = new Faker();

    private String id;

    private Name name;

    private String title;

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
        id = UUID.randomUUID().toString();
        name = faker.name();
        title = faker.name().title();
        email = faker.internet().emailAddress();
        phone = faker.phoneNumber().cellPhone();
        birth = faker.date().birthday(0,65);
        address = faker.address();
        company = faker.company().name();
        creditCardNumber = faker.idNumber().invalid();
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
                title,
                email,
                phone,
                dateFormat.format(birth),
                address.fullAddress(),
                address.streetName(),
                address.cityName(),
                address.zipCode(),
                address.country(),
                address.state(),
                company,
                creditCardNumber,
                jobTitle,
                dateFormat.format(startDate),
                dateFormat.format(endDate)
        };
    }
}
