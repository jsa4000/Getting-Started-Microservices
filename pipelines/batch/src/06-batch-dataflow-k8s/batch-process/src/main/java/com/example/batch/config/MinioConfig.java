package com.example.batch.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient client(@Value("${storage.url}") String url,
                              @Value("${storage.accessKey}") String accessKey,
                              @Value("${storage.secretKey}") String secretKey)
            throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(url, accessKey, secretKey);
    }

}
