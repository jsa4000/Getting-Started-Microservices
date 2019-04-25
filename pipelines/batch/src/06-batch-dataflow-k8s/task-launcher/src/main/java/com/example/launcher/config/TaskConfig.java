package com.example.launcher.config;

import com.example.launcher.listener.JobCompletionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TaskConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public Tasklet launcherTasklet() {
        return (StepContribution contribution, ChunkContext chunkContext) -> {
                log.info("This is a simple Job running.");
                //throw new Exception("Test the excetion so it fails");
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
