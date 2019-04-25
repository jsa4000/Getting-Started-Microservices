package com.example.notifier;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@EnableTask
@EnableBatchProcessing
@SpringBootApplication
public class NotificatorApplication {
    public static void main(String[] args) {  SpringApplication.run(NotificatorApplication.class, args); }
}
