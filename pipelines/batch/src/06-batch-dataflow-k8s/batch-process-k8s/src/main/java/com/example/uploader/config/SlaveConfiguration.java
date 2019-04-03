package com.example.uploader.config;

import com.example.uploader.batch.PersonEnrichProcessor;
import com.example.uploader.model.Person;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Profile("worker")
@Configuration
public class SlaveConfiguration {

    @Value("${batch.max-threads:4}")
    private int maxThreads;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    MinioClient client;

    @Bean
    public DeployerStepExecutionHandler stepExecutionHandler(ApplicationContext context,
                                                             JobExplorer jobExplorer,
                                                             JobRepository jobRepository) {
        return new DeployerStepExecutionHandler(context, jobExplorer, jobRepository);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader(@Value("#{stepExecutionContext['fileName']}") String source)
            throws Exception {
        log.info("Slave processing the file: " + source );

        String[] parts = source.split(":");
        String bucketName = parts[0];
        String objectName = parts[1];

        log.debug("Bucket Name: " + bucketName);
        log.debug("Object Name: " + objectName);

        client.statObject(bucketName, objectName);

        String slaveTempPath = tempPath + "/slave_data";
        File destDir = new File(slaveTempPath);

        log.info("Creating the temp directory.");

        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new Exception("Folder cannot be created: " + slaveTempPath);
        }

        log.info("Fetching the file to process locally.");

        InputStream inStream = client.getObject(bucketName, objectName);
        File targetFile = new File(slaveTempPath + "/" + objectName);
        OutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[16 * 1024];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close();

        Resource resource = resourceLoader.getResource("file:"  + targetFile);
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
    public JdbcBatchItemWriter<Person> writer(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person (first_name, last_name, department, group_name, update_time) VALUES (:firstName, :lastName, :department, :groupName, :updateTime)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step slaveStep(StepBuilderFactory stepBuilderFactory,
                          PersonEnrichProcessor processor) throws Exception {
        return stepBuilderFactory.get("slaveStep")
                .<Person, Person>chunk(10)
                .reader(reader(null))
                .processor(processor)
                .writer(writer(null))
                //.taskExecutor(taskExecutor())
                //.throttleLimit(20)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxThreads);
        //taskExecutor.setCorePoolSize(maxThreads);
        //taskExecutor.setQueueCapacity(maxThreads);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
