package com.example.auth.config;

import com.example.auth.actuator.CustomInfoContributor;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public InfoContributor infoContributor()  {
        return new CustomInfoContributor();
    }

}
