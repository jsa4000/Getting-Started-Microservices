package com.example.devices.utils;

public class SystemUtils {

    public static int getAvailableProcessors(boolean reserveMainThread) {
        int result = Runtime.getRuntime().availableProcessors();
        if (reserveMainThread) result -=1;
        return result == 0 ? 1 : result;
    }
}
