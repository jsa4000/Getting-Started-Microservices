package com.example.batch.config;

import com.example.batch.batch.PersonEnrichProcessor;
import com.example.batch.model.Person;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Profile("worker")
@Configuration
public class SlaveConfiguration {

    @Value("${batch.max-threads:4}")
    private int maxThreads;

    @Bean
    public DeployerStepExecutionHandler stepExecutionHandler(ApplicationContext context,
                                                             JobExplorer jobExplorer,
                                                             JobRepository jobRepository) {
        return new DeployerStepExecutionHandler(context, jobExplorer, jobRepository);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader(@Value("#{stepExecutionContext[localFile]}") Resource resource) {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .linesToSkip(1)
                .resource(resource)
                .delimited()
                .names(new String[]{"firstName", "lastName", "departmentId", "groupName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (first_name, last_name, department, group_name, update_time) VALUES (:firstName, :lastName, :department, :groupName, :updateTime)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name = "slaveStep")
    public Step slaveStep(StepBuilderFactory stepBuilderFactory,
                          PersonEnrichProcessor processor,
                          @Qualifier("slaveTaskExecutor") TaskExecutor taskExecutor) {

        return stepBuilderFactory.get("load")
                .<Person, Person>chunk(10)
                .reader(reader(null))
                .processor(processor)
                .writer(writer(null))
                //.taskExecutor(taskExecutor)
                //.throttleLimit(20)
                .build();
    }

    @Bean(name = "slaveTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxThreads);
        taskExecutor.setCorePoolSize(maxThreads);
        taskExecutor.setQueueCapacity(maxThreads);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
