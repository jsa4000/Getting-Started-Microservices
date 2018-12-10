package com.example.gateway.service;

import com.example.gateway.model.User;

import java.util.Optional;

public interface ManagementService {

    Optional<User> getUserById(String name);
}
