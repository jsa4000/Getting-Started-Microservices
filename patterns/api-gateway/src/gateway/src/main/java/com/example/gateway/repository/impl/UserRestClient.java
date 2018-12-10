package com.example.gateway.repository.impl;

import com.example.gateway.model.User;
import com.example.gateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class UserRestClient implements UserRepository {

    private final static String getUserByName_endpoint = "http://management/users/{id}";

    @Autowired
    private RestTemplate client;

    public Optional<User> getUserById(String id) {
        ResponseEntity<User> response = client.getForEntity(getUserByName_endpoint,User.class, id);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.of(response.getBody());
        }
        return Optional.empty();
    }
}
