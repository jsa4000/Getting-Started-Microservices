package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Properties;

@EnableRetry
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        properties.put("spring.profiles.active", "master");

        SpringApplication.run(BatchApplication.class, args);
    }

}
