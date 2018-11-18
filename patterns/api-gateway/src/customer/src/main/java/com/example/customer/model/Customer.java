package com.example.customer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("customer")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id @Getter private String id;
    @Indexed @Getter private String firstName;
    @Indexed @Getter private String lastName;
    @Getter private String email;
    @Getter private Address address;
}
