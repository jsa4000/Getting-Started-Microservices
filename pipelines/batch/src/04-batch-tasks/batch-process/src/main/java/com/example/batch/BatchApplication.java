package com.example.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Properties;

@EnableTask
@EnableRetry
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        properties.put("spring.profiles.active", "master");

        SpringApplication.run(BatchApplication.class, args);
    }

}
