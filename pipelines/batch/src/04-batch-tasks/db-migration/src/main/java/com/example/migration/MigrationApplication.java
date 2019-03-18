package com.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigrationApplication {
    private static final Logger log = LoggerFactory.getLogger(MigrationApplication.class);

    public static void main(String[] args) {
        log.info("Starting migration...");
        SpringApplication.run(MigrationApplication.class, args);
        log.info("Migration has finished.");
    }

}
