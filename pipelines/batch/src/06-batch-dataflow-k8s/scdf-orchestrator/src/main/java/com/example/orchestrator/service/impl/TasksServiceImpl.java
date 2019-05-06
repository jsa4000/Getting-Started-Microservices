package com.example.orchestrator.service.impl;

import com.example.orchestrator.domain.CustomTaskSchedule;
import com.example.orchestrator.repository.CustomTaskScheduleRepository;
import com.example.orchestrator.service.TasksService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TasksServiceImpl implements TasksService {

    private CustomTaskScheduleRepository repository;

    public TasksServiceImpl(CustomTaskScheduleRepository repository) {
        this.repository = repository;
    }

    public CustomTaskSchedule save(CustomTaskSchedule entity) {
        return repository.save(entity);
    }

    public Optional<CustomTaskSchedule> getById(Long id) {
        return repository.findById(id);
    }

    public Iterable<CustomTaskSchedule> getAll() {
        return repository.findAll();
    }

}
