package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String name);
}
