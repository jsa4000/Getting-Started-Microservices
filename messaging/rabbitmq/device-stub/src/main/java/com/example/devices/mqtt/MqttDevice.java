package com.example.devices.mqtt;

import com.example.devices.mqtt.handlers.ResponseHandler;
import com.example.devices.utils.SystemUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
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

    @Getter
    private AtomicLong totalMessagesReceivedCount = new AtomicLong();

    @Getter
    private AtomicLong totalMessagesPublishedCount = new AtomicLong();

    @Getter
    private AtomicLong totalMessagesErrorCount = new AtomicLong();

    private ResponseHandler responseHandler;
    private MqttAsyncClient mqttClient;

    private ExecutorService pool =
            Executors.newFixedThreadPool(SystemUtils.getAvailableProcessors(true));

    public boolean isConnected() { return mqttClient.isConnected(); }

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
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {log.info("Message arrived to topic {}", topic); }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { log.info("Message delivered"); }

                @Override
                public void connectionLost(Throwable cause) { log.error("Connection to MQTT lost: {}", cause); }

                @Override
                public void connectComplete(boolean reconnect, java.lang.String serverURI){
                    if(reconnect) {
                        log.info("Forcing Reconnection for device {}", deviceId);
                        resgisterDevices(mqttClient, responseHandler, deviceId);
                    }
                }
            });
            connect(deviceId, mqttClient, options, responseHandler);
            return mqttClient;
        } catch(MqttException e) {
            log.error("MQTT Client Connect Failure, exiting... {}", e);
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
                }
            });
            log.debug("Token from connection {}", iMqttToken);
        } catch(MqttException e) {
            log.error("MQTT Client Connect Failure, exiting...", e);
        }
    }

    private void resgisterDevices(MqttAsyncClient mqttClient, ResponseHandler responseHandler, String deviceId) {
        String topic = String.join("/", "devices", deviceId, "request", "#");
        try {
            mqttClient.subscribe(topic, 0,
                    (topicToHandle, messageToHandle) -> {
                        totalMessagesReceivedCount.incrementAndGet();
                        pool.execute(() -> handleMessage(topicToHandle, messageToHandle));
                    })
                    .setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            log.info("Client " + deviceId + " subscribed to topic " + topic);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            log.error("Failed subscribing to topic {} with client {}, exiting...", topic, deviceId);
                        }
                    })
            ;
        } catch (MqttException e) {
            log.error("Failed registering subscription to device topic {} with client {}, exiting...", topic, deviceId);
        }
    }

    private void handleMessage(String topic, MqttMessage message) {
        log.info("Handling message sent to device topic {}: {}", topic, message);

        String topicResponse = topic.replace("request", "response");
        MqttMessage responseMessage = responseHandler.onMessage(topic);
        responseMessage.setQos(0);

        try {
            log.info("Publishing response to device topic {}", topicResponse);
            mqttClient.publish(topicResponse, responseMessage);
            totalMessagesPublishedCount.incrementAndGet();
        }
        catch (Exception ex) {
            log.error("Failed publishing response to device topic {}", topicResponse);
            totalMessagesErrorCount.incrementAndGet();
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
