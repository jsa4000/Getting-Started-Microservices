package com.example.uploader.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.NoOpEnvironmentVariablesProvider;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("master")
@Configuration
public class MasterConfiguration {

    @Value("${batch.max-workers:1}")
    private int maxWorkers;

    @Bean
    public Step masterStep(StepBuilderFactory stepBuilderFactory,
                           Partitioner partitioner,
                           PartitionHandler partitionHandler) {
        return stepBuilderFactory.get("masterStep")
                .partitioner("slaveStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public DeployerPartitionHandler partitionHandler(@Value("${batch.worker-app}") String resourceLocation,
                                                     @Value("${spring.application.name}") String applicationName,
                                                     @Value("${spring.profiles.active}") String activeProfile,
                                                     ApplicationContext context,
                                                     TaskLauncher taskLauncher,
                                                     JobExplorer jobExplorer) {
        DockerResourceLoader resourceLoader = new DockerResourceLoader();
        Resource resource = resourceLoader.getResource(resourceLocation);
        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher,
                jobExplorer,resource,"slaveStep");

        log.info("Worker spring profile: " + activeProfile.replace("master","worker"));

        List<String> commandLineArgs = new ArrayList<>(3);
        commandLineArgs.add("--spring.profiles.active=" + activeProfile.replace("master","worker"));
        commandLineArgs.add("--spring.cloud.task.initialize.enable=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");

        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new NoOpEnvironmentVariablesProvider());
        partitionHandler.setMaxWorkers(maxWorkers);
        partitionHandler.setApplicationName(applicationName);

        return partitionHandler;
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(masterStep( null, null, null))
                .build();
    }
}
