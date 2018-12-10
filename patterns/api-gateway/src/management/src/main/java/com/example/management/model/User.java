package com.example.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id private String id;
    private String password;
    @Indexed(unique = true) private String email;
    private boolean active;
    private List<String> resources;
    private List<String> roles;
}
