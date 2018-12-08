package com.example.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Getter private String id;
    @Getter private String username;
    @Getter private String password;
    @Getter private String email;
    @Getter private boolean active;
    @Getter private List<String> resources;
    @Getter private List<String> roles;

}
