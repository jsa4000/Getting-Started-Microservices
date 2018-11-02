package com.example.devices.managers;

import com.example.devices.bootstrap.DevicesBootstrap;
import com.example.devices.data.DeviceData;
import com.example.devices.mqtt.MqttDevice;
import com.example.devices.mqtt.handlers.ResponseHandler;
import com.example.devices.utils.MacGenerator;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class DeviceManager {

    private HashMap<String,MqttDevice> devices = new HashMap<>();

    private List<Long> devicesCreated;

    @Autowired
    private DevicesBootstrap devicesBootstrap;

    @Autowired
    private ResponseHandler responseHandler;

    @Autowired
    private ConfigurationManager configurationManager;

    @Value("${spring.rabbitmq.addresses}")
    private String mqttAddresses;

    @Value("${devices.lastWillTopic:#{null}}")
    private String lastWillTopic;

    @Value("${spring.rabbitmq.username:#{null}}")
    private String username;

    @Value("${spring.rabbitmq.password:#{null}}")
    private String password;

    public List<DeviceData> getAll() {
        List<DeviceData> result = new ArrayList<>();
        devices.values()
                .forEach(device -> result.add(
                        new DeviceData(device.getDeviceId(),
                                String.valueOf(device.isConnected()),
                                device.getTotalMessagesReceivedCount().get(),
                                device.getTotalMessagesPublishedCount().get(),
                                device.getTotalMessagesErrorCount().get())));
        return result;
    }

    public DeviceData getDeviceById(String id) {
        Optional<MqttDevice> mqttDevice = devices.values().stream().filter(x -> x.getDeviceId().equals(id)).findFirst();
        if (mqttDevice.isPresent()) {
            return  new DeviceData(mqttDevice.get().getDeviceId(),
                    String.valueOf(mqttDevice.get().isConnected()),
                    mqttDevice.get().getTotalMessagesReceivedCount().get(),
                    mqttDevice.get().getTotalMessagesPublishedCount().get(),
                    mqttDevice.get().getTotalMessagesErrorCount().get());
        }
        return null;
    }

    @PostConstruct
    private void init() {
        Long fromMac = new Long(configurationManager.getPartitionId() * configurationManager.getDevicesCount());
        Long toMac = fromMac + configurationManager.getDevicesCount();

        devicesCreated = devicesBootstrap.createDevices(fromMac, toMac);
        log.info("Creating Devices from {} to {}",
                MacGenerator.getMacByNumber(fromMac),MacGenerator.getMacByNumber(toMac-1));
        devicesCreated.stream().forEach(device -> {
            String deviceMac = MacGenerator.getMacByNumber(device);
            log.info("Creating Device with MAC {}",deviceMac);
            if (Strings.isNullOrEmpty(username)) {
                devices.put(String.valueOf(device),
                        new MqttDevice(responseHandler, deviceMac, mqttAddresses, deviceMac , deviceMac, lastWillTopic));
            }
            else {
                devices.put(String.valueOf(device),
                        new MqttDevice(responseHandler, deviceMac, mqttAddresses, username , password, lastWillTopic));
            }
        });
    }

    @PreDestroy
    private void dispose() {
        devicesBootstrap.removeDevices(devicesCreated);
    }

}
