package com.example.orchestrator.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.dataflow.rest.client.TaskOperations;
import org.springframework.cloud.dataflow.rest.client.config.DataFlowClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@EnableAutoConfiguration(exclude = DataFlowClientAutoConfiguration.class)
public class DataFlowTestConfiguration {

    @Bean
    public TaskOperations taskOperations() {
        return mock(TaskOperations.class);
    }

}
