package com.example.orchestrator.controller;

import com.example.orchestrator.domain.CustomTaskSchedule;
import com.example.orchestrator.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    TasksService service;

    @GetMapping("/{id}")
    public ResponseEntity<CustomTaskSchedule> getById(@PathVariable int id) {
        return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
    }
}
