package com.example.management.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("security")
public class SecurityProperties {
    private boolean enabled = true;
    private String symmetricKey;
    private String resourceId;
}
