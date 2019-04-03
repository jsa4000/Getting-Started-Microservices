package com.example.uploader;

import com.example.uploader.utils.SimpleCommandArgsParser;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.retry.annotation.EnableRetry;

import java.util.HashMap;
import java.util.Properties;

@EnableTask
@EnableRetry
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        HashMap<String, String> commands = SimpleCommandArgsParser.parse(args);
        if (commands.containsKey("--inputFile")) {
            properties.put("batch.inputFile", commands.get("--inputFile"));
        }
        if (commands.containsKey("--resourcesPath")) {
            properties.put("batch.resourcesPath", commands.get("--resourcesPath"));
        }
        SpringApplication.run(BatchApplication.class, args);
    }

}
