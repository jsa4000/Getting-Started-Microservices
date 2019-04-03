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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void run(String... args) {
        log.info("Notifier Task has started");

        HashMap<String, String> commands = SimpleCommandArgsParser.parse(args);

        String title = "Notification Email";
        String inputFile ="Unknown";
        String resourcesPath ="Unknown";

        if (commands.containsKey("--inputFile")) {
            inputFile = commands.get("--inputFile");
        }
        if (commands.containsKey("--resourcesPath")) {
            resourcesPath = commands.get("--resourcesPath");
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("inputFile", inputFile);
        model.put("resourcesPath", resourcesPath);
        model.put("args", args);

        Context context = new Context();
        context.setVariables(model);
        String html = templateEngine.process("email-template", context);

        log.debug(html);

        service.sendMail("jsantosa@minsait.com", "jsantosa.minsait@gmail.com","Task Notifier", html);
        log.info("Notifier Task has finished");
    }
}
