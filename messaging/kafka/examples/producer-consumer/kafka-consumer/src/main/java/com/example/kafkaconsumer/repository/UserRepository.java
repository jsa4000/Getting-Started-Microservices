package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    List<User> findByEmail(String email);
}
