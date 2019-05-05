package com.example.orchestrator.service;

import com.example.orchestrator.model.CustomTaskSchedule;
import org.springframework.stereotype.Component;

@Component
public class TasksServiceImpl implements TasksService {

    public CustomTaskSchedule getById(int id) {
        return new CustomTaskSchedule();
    }
    
}
