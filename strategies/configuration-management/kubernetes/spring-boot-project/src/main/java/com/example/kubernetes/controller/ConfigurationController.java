package com.example.kubernetes.controller;

import com.example.kubernetes.config.DataBaseProperties;
import com.example.kubernetes.web.api.ConfigurationsApiDelegate;
import com.example.kubernetes.web.api.model.ConfigurationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ConfigurationController implements ConfigurationsApiDelegate {

    private DataBaseProperties dataBaseProperties;

    public ConfigurationController(DataBaseProperties dataBaseProperties) {
        this.dataBaseProperties = dataBaseProperties;
    }

    @Override
    public ResponseEntity<ConfigurationDto> getConfiguration() {
        return ResponseEntity.ok(new ConfigurationDto()
                .url(dataBaseProperties.getUrl())
                .username(dataBaseProperties.getUsername())
                .password(dataBaseProperties.getPassword())
                .driverClass(dataBaseProperties.getDriverClassName()));
    }

}
