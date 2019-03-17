package com.example.batch.config;

import com.example.batch.listener.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${batch.max-threads:4}")
    private int maxThreads;

     @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   JobCompletionNotificationListener listener,
                   @Qualifier("step1") Step step1,
                   @Qualifier("step2") Step step2) {

         final Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                 .start(step2)
                 .build();

        final Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(step1)
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(parallelFlow)
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(maxThreads);
        return taskExecutor;
    }
}
