package com.example.gateway.service.impl;

import com.example.gateway.model.User;
import com.example.gateway.repository.UserRepository;
import com.example.gateway.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagementServiceDefault implements ManagementService {

    @Autowired
    private UserRepository repository;

    public Optional<User> getUserById(String id) {
        return repository.getUserById(id);
    }
}
