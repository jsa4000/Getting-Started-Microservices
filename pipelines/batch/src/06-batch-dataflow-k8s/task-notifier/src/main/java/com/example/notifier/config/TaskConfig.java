package com.example.notifier.config;

import com.example.notifier.listener.JobCompletionListener;
import com.example.notifier.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class TaskConfig {

    public static final String[] PARAMS_FILTER = new String[] {"user","pass","key","secret"};

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    EmailService service;

    @Value("${mail.message:}")
    private String message;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Bean
    @StepScope
    public Tasklet launcherTasklet() {
        return (StepContribution contribution, ChunkContext chunkContext) -> {
            log.info("Notifier Task has started");

            JobParameters params = chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobParameters();

            String title = "Notification Email: " + message;
            String inputFile = params.getString("--inputFile", "Unknown");
            String resourcesPath = params.getString("--resourcesPath", "Unknown");

            Map<String, Object> model = new HashMap<>();
            model.put("title", title);
            model.put("inputFile", inputFile);
            model.put("resourcesPath", resourcesPath);
            model.put("args", params.getParameters()
                    .entrySet().stream()
                    .filter(x -> Arrays.asList(PARAMS_FILTER).contains(x.getKey()) )
                    .map(x -> x.getKey() + " : " + x.getValue())
                    .collect(Collectors.toList()));

            Context context = new Context();
            context.setVariables(model);
            String html = templateEngine.process("email-template", context);

            log.debug(html);

            service.sendMail("jsantosa@minsait.com", "jsantosa.minsait@gmail.com","Task Notifier", html);
            log.info("Notifier Task has finished");

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job(JobCompletionListener listener) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderFactory.get("jobStep1")
                        .tasklet(launcherTasklet()).build())
                .listener(listener)
                .build();
    }

}
