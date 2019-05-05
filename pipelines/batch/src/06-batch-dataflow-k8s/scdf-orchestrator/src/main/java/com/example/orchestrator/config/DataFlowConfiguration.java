package com.example.orchestrator.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.rest.client.DataFlowOperations;
import org.springframework.cloud.dataflow.rest.client.DataFlowTemplate;
import org.springframework.cloud.dataflow.rest.client.TaskOperations;
import org.springframework.cloud.dataflow.rest.util.HttpClientConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(DataFlowProperties.class)
public class DataFlowConfiguration {
    private static Log logger = LogFactory.getLog(DataFlowConfiguration.class);

    @Autowired
    private DataFlowProperties properties;

    @Bean
    public TaskOperations taskOperations(DataFlowOperations dataFlowOperations) {
        return dataFlowOperations.taskOperations();
    }

    @Bean
    public DataFlowOperations dataFlowOperations() {
        final RestTemplate restTemplate = DataFlowTemplate.getDefaultDataflowRestTemplate();
        validateUsernamePassword(this.properties.getDataflowServerUsername(), this.properties.getDataflowServerPassword());
        if (StringUtils.hasText(this.properties.getDataflowServerUsername())
                && StringUtils.hasText(this.properties.getDataflowServerPassword())) {
            restTemplate.setRequestFactory(HttpClientConfigurer.create(this.properties.getDataflowServerUri())
                    .basicAuthCredentials(properties.getDataflowServerUsername(), properties.getDataflowServerPassword())
                    .buildClientHttpRequestFactory());
            logger.debug("Configured basic security for accessing the Data Flow Server");
        }
        else {
            logger.debug("Not configuring basic security for accessing the Data Flow Server");
        }
        return new DataFlowTemplate(this.properties.getDataflowServerUri(), restTemplate);
    }

    private void validateUsernamePassword(String userName, String password) {
        if (!StringUtils.isEmpty(password) && StringUtils.isEmpty(userName)) {
            throw new IllegalArgumentException("A password may be specified only together with a username");
        }
        if (StringUtils.isEmpty(password) && !StringUtils.isEmpty(userName)) {
            throw new IllegalArgumentException("A username may be specified only together with a password");
        }
    }
}
