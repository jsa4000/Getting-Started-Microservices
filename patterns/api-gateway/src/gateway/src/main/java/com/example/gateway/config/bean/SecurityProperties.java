package com.example.gateway.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("security")
public class SecurityProperties {
    public enum AuthorityType { basic, normal, trusted }

    private boolean enabled = true;
    private String symmetricKey = StringUtils.EMPTY;
    private String resourceId = StringUtils.EMPTY;
    private int accessTokenValidity = Integer.MAX_VALUE;
    private int refreshTokenValidity = Integer.MAX_VALUE;
    private Map<AuthorityType, AuthorityProperties> authorities;
}
