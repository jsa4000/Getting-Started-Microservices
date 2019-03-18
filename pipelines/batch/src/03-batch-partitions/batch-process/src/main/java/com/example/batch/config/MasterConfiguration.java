package com.example.batch.config;

import com.example.batch.batch.CustomMultiResourcePartitioner;
import com.example.batch.batch.PostProcessTasklet;
import com.example.batch.batch.PreProcessTasklet;
import com.example.batch.listener.JobCompletionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;

@Slf4j
@EnableRetry
@Configuration
@EnableBatchProcessing
public class MasterConfiguration {

    @Value("${batch.max-threads:4}")
    private int maxThreads;

    @Autowired
    ResourcePatternResolver resoursePatternResolver;

    @Bean
    @StepScope
    public CustomMultiResourcePartitioner partitioner(
            @Value("${batch.pattern}") String pattern) {
        CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
        try {
            Resource[] resources = resoursePatternResolver.getResources("file:" + pattern);
            log.info("Current files to process are: " + resources.length);
            partitioner.setResources(resources);
            //partitioner.partition(5);
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving"
                    + " the input file pattern.", e);
        }
        return partitioner;
    }

    @Bean
    public Step partitionStep(StepBuilderFactory stepBuilderFactory,
                              @Qualifier("slaveStep") Step slaveStep,
                              @Qualifier("masterTaskExecutor") TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("partitionStep")
                .partitioner("slaveStep", partitioner(null))
                .step(slaveStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step postProcessStep(StepBuilderFactory stepBuilderFactory, PostProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("postProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step preProcessStep(StepBuilderFactory stepBuilderFactory, PreProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("preProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, JobCompletionListener listener) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                //.start(preProcessStep(null,null))
                .start(partitionStep(null, null, null))
                //.next(postProcessStep(null,null))
                .build();
    }

    @Bean(name = "masterTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxThreads);
        taskExecutor.setCorePoolSize(maxThreads);
        taskExecutor.setQueueCapacity(maxThreads);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
