package com.example.notifier;

import com.example.notifier.services.EmailService;
import com.example.notifier.utils.SimpleCommandArgsParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.retry.annotation.EnableRetry;

import java.util.HashMap;

@Slf4j
@EnableTask
@EnableRetry
@SpringBootApplication
public class NotificatorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NotificatorApplication.class, args);
    }

    @Autowired
    EmailService service;

    @Override
    public void run(String... args) throws Exception {
        log.info("Notifier Task has started");

        HashMap<String, String> commands = SimpleCommandArgsParser.parse(args);

        StringBuilder builder = new StringBuilder();
        builder.append("This is a test message.\n");
        builder.append(args.toString());
        if (commands.containsKey("--inputFile")) {
           builder.append("--inputFile==" + commands.get("--inputFile") + "\n");
        }
        if (commands.containsKey("--resourcesPath")) {
            builder.append("--resourcesPath==" + commands.get("--resourcesPath") + "\n");
        }
        builder.append("End.\n");
        service.sendMail("jsantosa.minsait@gmail.com","jsantosa@minsait.com","Task Notifier",builder.toString());
        log.info("Notifier Task has finished");
    }
}
