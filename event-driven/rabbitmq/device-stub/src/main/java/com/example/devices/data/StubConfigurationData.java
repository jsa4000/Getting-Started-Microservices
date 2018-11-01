package com.example.devices.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StubConfigurationData {

    private int partitionId;

    private int devicesCount;
}
