package com.example.batch.config;

import com.example.batch.batch.PostProcessTasklet;
import com.example.batch.batch.PreProcessTasklet;
import com.example.batch.listener.JobCompletionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.CommandLineArgsProvider;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.NoOpEnvironmentVariablesProvider;
import org.springframework.cloud.task.batch.partition.SimpleCommandLineArgsProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("master")
@Configuration
public class MasterConfiguration {

    @Bean
    public Partitioner partitioner(ResourcePatternResolver resourcePatternResolver,
                                   @Value("${batch.pattern}") String resourcePath) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("file:" + resourcePath);
        log.info("Current files to process are: " + resources.length);
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setResources(resources);
        return partitioner;
    }

    @Bean
    public Step masterStep(StepBuilderFactory stepBuilderFactory,
                              Partitioner partitioner,
                              PartitionHandler partitionHandler) {
        return stepBuilderFactory.get("masterStep")
                .partitioner("load", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public SimpleCommandLineArgsProvider commandLineArgsProvider() {
        SimpleCommandLineArgsProvider provider = new SimpleCommandLineArgsProvider();

        List<String> commandLineArgs = new ArrayList<>(4);
        commandLineArgs.add("--spring.profiles.active=worker");
        commandLineArgs.add("--spring.cloud.task.initialize.enable=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");
        commandLineArgs.add("--spring.datasource.initialize=false");
        provider.setAppendedArgs(commandLineArgs);

        return provider;
    }

    @Bean
    public DeployerPartitionHandler partitionHandler(@Value("${batch.worker-app}") String resourceLocation,
                                                     @Value("${spring.application.name}") String applicationName,
                                                     ApplicationContext context,
                                                     TaskLauncher taskLauncher,
                                                     JobExplorer jobExplorer,
                                                     CommandLineArgsProvider commandLineArgsProvider) {
        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher,
                        jobExplorer,
                        context.getResource("file:" + resourceLocation),
                        "load");

        partitionHandler.setCommandLineArgsProvider(commandLineArgsProvider);
        partitionHandler.setEnvironmentVariablesProvider(new NoOpEnvironmentVariablesProvider());
        partitionHandler.setMaxWorkers(2);
        partitionHandler.setApplicationName(applicationName);

        return partitionHandler;
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
                .start(masterStep( null, null, null))
                //.next(postProcessStep(null,null))
                .build();
    }
}
