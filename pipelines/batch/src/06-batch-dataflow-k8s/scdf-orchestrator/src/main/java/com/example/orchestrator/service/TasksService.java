package com.example.orchestrator.service;

import com.example.orchestrator.domain.CustomTaskSchedule;

public interface TasksService {
     CustomTaskSchedule getById(int id);
}
