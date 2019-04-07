package com.example.process.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("worker")
@Configuration
public class SlaveConfiguration {

    @Bean
    public DeployerStepExecutionHandler stepExecutionHandler(ApplicationContext context,JobExplorer jobExplorer,
                                                             JobRepository jobRepository ) {
        return new DeployerStepExecutionHandler(context, jobExplorer, jobRepository);
    }

    @Bean
    @StepScope
    public Tasklet workerTasklet(final @Value("#{stepExecutionContext['fileName']}")String fileName) {
        return (contribution, chunkContext) -> {
            System.out.println("This tasklet ran partition: " + fileName);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step slaveStep(StepBuilderFactory stepBuilderFactory) throws Exception {
        return stepBuilderFactory.get("slaveStep")
                .tasklet(workerTasklet(null))
                .build();
    }
}
