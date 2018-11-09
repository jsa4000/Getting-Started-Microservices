package com.example.kafkaconsumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.kafkaconsumer.repository")
public class MongoDBConfig {

}
