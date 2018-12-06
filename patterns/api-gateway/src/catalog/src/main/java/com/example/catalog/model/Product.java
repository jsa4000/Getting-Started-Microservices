package com.example.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("products")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id @Getter private String id;
    @Indexed @Getter private String name;
    @Getter private String description;
    @Getter private String model;
    @Getter private String brand;
    @Getter private String imageUrl;
}
