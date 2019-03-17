package com.example.batch.config;

import com.example.batch.batch.PersonEnrichProcessor;
import com.example.batch.batch.PersonItemProcessor;
import com.example.batch.batch.PostProcessTasklet;
import com.example.batch.batch.PreProcessTasklet;
import com.example.batch.listener.JobCompletionListener;
import com.example.batch.listener.SkipProcessListener;
import com.example.batch.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;

import javax.sql.DataSource;

@EnableRetry
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${batch.max-threads:4}")
    private int maxThreads;

    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader(@Value("${batch.filename:sample-data.csv}") String filename) {
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
    public JdbcBatchItemWriter<Person> writer(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (first_name, last_name, department, update_time) VALUES (:firstName, :lastName, :department, :updateTime)")
                .dataSource(dataSource)
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
    public Step step11(StepBuilderFactory stepBuilderFactory,
                     PersonEnrichProcessor processor,
                     SkipProcessListener skipListener,
                     TaskExecutor taskExecutor) {

        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(reader(null))
                .processor(processor)
                .writer(writer(null))
                .taskExecutor(taskExecutor)
                .throttleLimit(20)
                .build();
    }

    @Bean
    public Step step12(StepBuilderFactory stepBuilderFactory,
                     PersonItemProcessor processor,
                     SkipProcessListener skipListener,
                     TaskExecutor taskExecutor) {

        return stepBuilderFactory.get("step2")
                .<Person, Person>chunk(100)
                .reader(reader(null))
                .processor(processor)
                .faultTolerant()
                .skipLimit(10)
                .skip(RuntimeException.class)
                .retryLimit(2)
                .retry(RuntimeException.class)
                .listener(skipListener)
                .writer(writer(null))
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, JobCompletionListener listener) {

        final Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(step12(null,null,null,null))
                .build();

        final Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(step11(null,null,null,null))
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(preProcessStep(null,null))
                .next(parallelFlow)
                .next(postProcessStep(null,null))
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
