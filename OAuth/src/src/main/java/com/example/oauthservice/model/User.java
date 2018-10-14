package com.example.oauthservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String username;
    private String password;
}
