package com.example.management.service;

import com.example.management.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(String id);

    User save(User user);

    void delete(String id);
}
