package com.example.orchestrator.service;

import com.example.orchestrator.domain.CustomTaskSchedule;

import java.util.Optional;

public interface TasksService {

     /**
      * Create or Update a Custom Task Service
      *
      * @param entity
      * @return
      */
     CustomTaskSchedule save(CustomTaskSchedule entity);

     /**
      * Get a Custom Task Service by Id
      *
      * @param id
      * @return
      */
     Optional<CustomTaskSchedule> getById(Long id);

     /**
      * Get all Custom Task Services
      *
      * @return
      */
     Iterable<CustomTaskSchedule> getAll();
}
