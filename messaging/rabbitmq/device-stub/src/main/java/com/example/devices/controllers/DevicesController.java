package com.example.devices.controllers;

import com.example.devices.data.DeviceData;
import com.example.devices.managers.DeviceManager;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DevicesController {

    @Autowired
    private DeviceManager deviceManager;

    @ApiOperation(value = "Get a List of Devices")
    @GetMapping("/")
    public ResponseEntity<List> getAll(){
        return ResponseEntity.ok(deviceManager.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceData> getDeviceById(@PathVariable(value = "id") String id){
        DeviceData device = deviceManager.getDeviceById(id);
        if (device == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.ok(device);
    }
}
