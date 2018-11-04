package com.example.devices.mqtt.enums;

import org.apache.commons.lang3.StringUtils;

public enum RequestEnum {
    GET_SENSOR_INFO("/get/info", 0, 1000, "messages/get_sensor_info.json"),
    GET_TEMPERATURE_VALUES("/get/temperature",0, 1000, "messages/get_temperature_values.json"),
    UNKNOWN(StringUtils.EMPTY, 0, 0, "messages/unknown_message.json");

    private String urlIdentifier;

    private int minResponseTime;

    private int maxResponseTime;

    private String responseFile;

    RequestEnum(String urlIdentifier, int minResponseTime, int maxResponseTime, String responseFile) {
        this.urlIdentifier = urlIdentifier;
        this.minResponseTime = minResponseTime;
        this.maxResponseTime = maxResponseTime;
        this.responseFile = responseFile;
    }

    public String getUrlIdentifier() {
        return urlIdentifier;
    }

    public int getMinResponseTime() {
        return minResponseTime;
    }

    public int getMaxResponseTime() {
        return maxResponseTime;
    }

    public String getResponseFile() {
        return responseFile;
    }
}
