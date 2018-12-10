package com.example.gateway.config;

import com.example.gateway.repository.impl.UserManagementClient;
import com.example.gateway.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new UserManagementClient();
    }

}
