package com.example.devices.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceData {
    private String deviceId;
    private String status;
    private Long totalMessagesReceived;
    private Long totalMessagesPublished;
    private Long totalMessagesError;
}
