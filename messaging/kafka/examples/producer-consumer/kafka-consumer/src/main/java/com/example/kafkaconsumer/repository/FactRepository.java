package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.model.Fact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FactRepository extends MongoRepository<Fact, String> {
}
