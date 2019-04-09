package com.example.process.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Bean
    public MinioClient client(@Value("${batch.storage.url}") String url,
                              @Value("${batch.storage.accessKey}") String accessKey,
                              @Value("${batch.storage.secretKey}") String secretKey,
                              @Value("${batch.storage.region:}") String region)
            throws InvalidPortException, InvalidEndpointException {
        log.info("The region configured is " + region);
        return new MinioClient(url, accessKey, secretKey,region);
    }

}
