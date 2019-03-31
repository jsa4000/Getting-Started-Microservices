package com.example.generator;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
public class PersonBean {

    @CsvBindByPosition(position = 0)
    @CsvBindByName
    private String id;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "first_name")
    private String firstName;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "last_name")
    private String lastName;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "full_name")
    private String fullName;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "gender")
    private String gender;

    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "email")
    private String email;

    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "phone_number")
    private String phone;

    @CsvBindByPosition(position = 7)
    @CsvBindByName(column = "birth_date")
    private Date birth;

    @CsvBindByPosition(position = 8)
    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByPosition(position = 9)
    @CsvBindByName(column = "street_name")
    private String street;

    @CsvBindByPosition(position = 10)
    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByPosition(position = 11)
    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByPosition(position = 12)
    @CsvBindByName(column = "state")
    private String state;

    @CsvBindByPosition(position = 13)
    @CsvBindByName(column = "zip_code")
    private String zipCode;

    @CsvBindByPosition(position = 14)
    @CsvBindByName(column = "company_name")
    private String company;

    @CsvBindByPosition(position = 15)
    @CsvBindByName(column = "credit_card")
    private String creditCardNumber;

    @CsvBindByPosition(position = 16)
    @CsvBindByName(column = "job_title")
    private String jobTitle;

    @CsvBindByPosition(position = 17)
    @CsvBindByName(column = "start_date")
    private Date startDate;

    @CsvBindByPosition(position = 18)
    @CsvBindByName(column = "end_date")
    private Date endDate;

    public PersonBean() {
        Faker faker = new Faker();
        id = faker.idNumber().valid();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        fullName = faker.name().fullName();
        gender = faker.name().title();
        email = faker.internet().emailAddress();
        phone = faker.phoneNumber().cellPhone();
        birth = faker.date().birthday(0,65);

        Address addr = faker.address();
        address = addr.fullAddress();
        street = addr.streetName();
        city = addr.cityName();
        zipCode = addr.zipCode();
        country = addr.country();
        state = addr.state();

        company = faker.company().name();
        creditCardNumber = faker.idNumber().ssnValid();
        jobTitle = faker.job().title();
        startDate = faker.date().past(1000, TimeUnit.DAYS);
        endDate = faker.date().past(25, TimeUnit.HOURS);

    }
}
