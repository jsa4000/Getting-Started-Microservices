package com.example.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class CustomTaskConfigurer extends DefaultTaskConfigurer {
    @Autowired
    public CustomTaskConfigurer(DataSource dataSource) {
        super(dataSource);
    }
}