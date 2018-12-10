package com.example.gateway.repository;

import com.example.gateway.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getUserById(String username);

}
