package com.example.gateway.service.impl;

import com.example.gateway.model.User;
import com.example.gateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagementRepositoryService {

    @Autowired
    private UserRepository repository;

    public Optional<User> getUserById(String name) {
        return repository.getUserById(name);
    }
}
