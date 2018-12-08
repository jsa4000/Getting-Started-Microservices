package com.example.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id private String id;
    @Indexed(unique = true) private String name;
}
