package com.example.devices.managers;

import com.example.devices.bootstrap.RegistryBootstrap;
import com.example.devices.data.StubConfigurationData;
import com.example.devices.exceptions.ConfigurationRegistryException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Controller
public class ConfigurationManager {

    @Autowired
    private RegistryBootstrap registryBootstrap;

    @Getter
    @Value("${devices.registry.enabled:false}")
    private boolean registryEnabled;

    @Getter
    @Value("${devices.environment:local}")
    private String environment;

    @Getter
    @Value("${devices.parameters.partitionId:0}")
    private int partitionId;

    @Getter
    @Value("${devices.parameters.count:10}")
    private int devicesCount;

    @PostConstruct
    private void init() throws ConfigurationRegistryException {
        if (registryEnabled) {
            StubConfigurationData configurationData = registryBootstrap.register();
            if (configurationData == null) {
                throw new ConfigurationRegistryException("Failed to register current Devices Stub Instance");
            }
            partitionId = configurationData.getPartitionId();
            devicesCount = configurationData.getDevicesCount();
         }
    }

    @PreDestroy
    private void dispose() {
        if (registryEnabled) {
            registryBootstrap.unregister();
        }
    }


}
