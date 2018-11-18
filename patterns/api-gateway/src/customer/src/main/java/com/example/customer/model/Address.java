package com.example.customer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Getter private int houseNumber;
    @Getter private String streetAddress;
    @Getter private String city;
    @Getter private String state;
    @Getter private String zipCode;
    @Getter private String country;
}
