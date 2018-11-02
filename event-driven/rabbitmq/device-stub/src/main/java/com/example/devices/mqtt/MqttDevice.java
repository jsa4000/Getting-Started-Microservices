package com.example.devices.mqtt;

import com.example.devices.mqtt.handlers.ResponseHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
public class MqttDevice {

    @Getter
    private String deviceId;

    @Getter
    private String mqttAddresses;

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private String topicLWT;

    private ResponseHandler responseHandler;
    private MqttAsyncClient mqttClient;

    private ExecutorService pool =  Executors.newFixedThreadPool(getProcessors());

    public boolean isConnected() { return mqttClient.isConnected(); }

    private static int getProcessors() {
        int result = Runtime.getRuntime().availableProcessors();
        return result == 1 ? 1 : result;
    }

    public MqttDevice(ResponseHandler responseHandler, String deviceId, String mqttAddresses,
                      String username, String password, String topicLWT ) {
        this.deviceId = deviceId;
        this.mqttAddresses = mqttAddresses;
        this.username = username;
        this.password = password;
        this.responseHandler = responseHandler;
        this.topicLWT = topicLWT;

        this.mqttClient = createClient();
    }

    private MqttAsyncClient createClient() {
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(15);
        options.setMaxInflight(65535);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setWill(topicLWT, String.format("Device Disconnected %s! (LWT)", deviceId).getBytes(), 0, true);

        String[] addresses = getEventsAddresses();
        options.setServerURIs(addresses);

        try {
            MqttAsyncClient mqttClient = new MqttAsyncClient(addresses[0], deviceId, new MemoryPersistence());
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    log.info("Message arrived to topic {}", topic);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("Message delivered");
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.error("Connection to MQTT lost [{}]", cause);

                }
            });
            connect(deviceId, mqttClient, options, responseHandler);
            return mqttClient;
        } catch(MqttException e) {
            log.error("MQTT Client Connect Failure, exiting... {}", e);
            System.exit(1);
            return null;
        }
    }

    private void connect(String clientId, MqttAsyncClient client, MqttConnectOptions options, ResponseHandler responseHandler) {

        log.debug("Trying to connect {}@{} with options {}", clientId, options.getServerURIs(), options);
        try {

            IMqttToken iMqttToken = client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    log.info("Device {} Connected!", deviceId);
                    resgisterDevices(client,  responseHandler, clientId);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    log.error("MQTT Client Connect Failure [{}] not retrying ...", exception);
                    //connect(clientId, deviceId, client, options);
                }
            });
            log.debug("Token from connection {}", iMqttToken);
        } catch(MqttException e) {
            log.error("MQTT Client Connect Failure, exiting...", e);
            System.exit(1);
        }
    }

    private void resgisterDevices(MqttAsyncClient mqttClient, ResponseHandler responseHandler, String deviceId) {
        String topic = String.join("/", "devices", deviceId, "request", "#");
        try {
            mqttClient.subscribe(topic, 0, (topic1, message) -> {
                pool.execute( () -> {
                    log.info("Handling message sent to device topic {}", topic1);
                    log.info("Message Received from Topic {}", message);

                    String topicResponse = topic1.replace("request", "response");
                    MqttMessage responseMessage = responseHandler.onMessage(topic1);
                    responseMessage.setQos(0);

                    try {
                        log.info("Publishing response to device topic {}", topicResponse);
                        mqttClient.publish(topicResponse, responseMessage);
                    }
                    catch (Exception ex) {
                        log.error("Failed publishing response to device topic {}", topicResponse);
                    }
                });
            }).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    log.info("Client " + deviceId + " subscribed to topic " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    log.error("Failed subscribing to topic {} with client {}, exiting...", topic, deviceId);
                    System.exit(1);
                }
            })
            ;
        } catch (MqttException e) {
            log.error("Failed registering subscription to device topic {} with client {}, exiting...", topic, deviceId);
            System.exit(1);
        }
    }

    private String[] getEventsAddresses() {
        return addProtocol(mqttAddresses.trim());
    }

    private static String[] addProtocol(String address) {
        return Stream.of(address.split(","))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(MqttDevice::addProtocolToAddresses)
                .toArray(String[]::new);
    }

    private static String addProtocolToAddresses(String address) {
        if(address.startsWith("tcp://") || address.startsWith("ssl://")) {
            return address;
        } else {
            return "ssl://" + address;
        }
    }
}
