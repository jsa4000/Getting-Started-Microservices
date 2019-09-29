package com.example.kubernetes.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseProperties {

    public static final String DEFAULT_DATABASE_PROPERTIES = "batch.datasource";

    private String url;
    private String username;
    private String password;
    private String driverClassName;

}