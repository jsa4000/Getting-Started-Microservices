package com.example.process.config;

import com.example.process.batch.*;
import com.example.process.listener.JobCompletionListener;
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
import org.springframework.core.io.ResourceLoader;

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
                                                     JobExplorer jobExplorer,
                                                     ResourceLoaderResolver resolver) {
        ResourceLoader resourceLoader = resolver.get(resourceLocation);
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
    public Step postProcessStep(StepBuilderFactory stepBuilderFactory, PostProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("postProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step downloadProcessStep(StepBuilderFactory stepBuilderFactory, DownloadProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("downloadProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step unzipProcessStep(StepBuilderFactory stepBuilderFactory, UnzipProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("unzipProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Step uploadProcessStep(StepBuilderFactory stepBuilderFactory, UploadProcessTasklet tasklet) {
        return stepBuilderFactory
                .get("uploadProcessStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, JobCompletionListener listener) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(downloadProcessStep(null,null))
                .next(unzipProcessStep(null,null))
                .next(uploadProcessStep(null,null))
                .next(masterStep( null, null, null))
                //.next(postProcessStep(null,null))
                .build();
    }
}
