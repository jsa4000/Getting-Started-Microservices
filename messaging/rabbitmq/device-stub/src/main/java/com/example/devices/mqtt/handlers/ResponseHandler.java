package com.example.devices.mqtt.handlers;

import com.example.devices.mqtt.enums.RequestEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Controller
public class ResponseHandler {

    public MqttMessage onMessage(String topic) {

        RequestEnum request = identifyRequestByTopic(topic);
        String response = readResponse(request);
        log.info(response);

        return new MqttMessage(response.getBytes());
    }

    private String readResponse(RequestEnum request) {

        try {
            log.info(request.getResponseFile());
            InputStream responseStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(request.getResponseFile());
            return IOUtils.toString(responseStream,"UTF-8");

        } catch (FileNotFoundException e) {
            log.warn("Cannot read file {}",e);
        } catch (IOException e) {
            log.warn("Cannot read file {}",e);
        }
        return StringUtils.EMPTY;
    }

    private RequestEnum identifyRequestByTopic(String topic) {
        if ( topic.contains(RequestEnum.GET_SENSOR_INFO.getUrlIdentifier())) {
            return RequestEnum.GET_SENSOR_INFO;
        } else if ( topic.contains(RequestEnum.GET_TEMPERATURE_VALUES.getUrlIdentifier())) {
            return RequestEnum.GET_TEMPERATURE_VALUES;
        } else {
            return RequestEnum.UNKNOWN;
        }
    }
}
