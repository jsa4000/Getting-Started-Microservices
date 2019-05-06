package com.example.orchestrator.repository;

import com.example.orchestrator.domain.CustomTaskSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTaskScheduleRepository extends JpaRepository<CustomTaskSchedule,Long> {
}
