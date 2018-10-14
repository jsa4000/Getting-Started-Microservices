package com.example.oauthservice.repository;

import com.example.oauthservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUsername(String username);
}
