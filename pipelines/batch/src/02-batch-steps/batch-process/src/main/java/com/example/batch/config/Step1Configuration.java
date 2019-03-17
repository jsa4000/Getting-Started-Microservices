package com.example.batch.config;

import com.example.batch.batch.PersonItemProcessor;
import com.example.batch.listener.SkipProcessListener;
import com.example.batch.model.Person;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
public class Step1Configuration {

    @Bean
    @StepScope
    public FlatFileItemReader<Person> readerStep1(@Value("${batch.resource:sample-data.csv}") String filename) {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .linesToSkip(1)
                .resource(new ClassPathResource(filename))
                .delimited()
                .names(new String[]{"firstName", "lastName", "departmentId"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Person> writerStep1(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (first_name, last_name, department, update_time) VALUES (:firstName, :lastName, :department, :updateTime)")
                .dataSource(dataSource)
                .build();
    }

    @Bean(name = "step1")
    public Step step(StepBuilderFactory stepBuilderFactory,
                      PersonItemProcessor processor,
                      SkipProcessListener skipListener,
                      TaskExecutor taskExecutor) {

        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(readerStep1(null))
                .processor(processor)
                .faultTolerant()
                .skipLimit(10)
                .skip(RuntimeException.class)
                .retryLimit(2)
                .retry(RuntimeException.class)
                .listener(skipListener)
                .writer(writerStep1(null))
                .taskExecutor(taskExecutor)
                .build();
    }

}
