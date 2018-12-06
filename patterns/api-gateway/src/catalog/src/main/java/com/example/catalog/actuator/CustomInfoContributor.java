package com.example.catalog.actuator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> userDetails = new HashMap<>();
        String hostName = StringUtils.EMPTY;
        try { hostName = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException ex) {}
        userDetails.put("host", hostName);
        builder.withDetail("host", userDetails);
    }

}