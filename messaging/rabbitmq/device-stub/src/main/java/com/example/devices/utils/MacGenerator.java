package com.example.devices.utils;

import org.apache.commons.lang3.StringUtils;

public class MacGenerator {

    public static String getMacByNumber(Long number) {
        String hexMac = StringUtils.leftPad(Long.toHexString(number), 12, "0");
        return String.format("%s:%s:%s:%s:%s:%s",
                hexMac.substring(0,2), hexMac.substring(2,4), hexMac.substring(4,6),
                hexMac.substring(6,8), hexMac.substring(8,10),  hexMac.substring(10,12));
    }

}
