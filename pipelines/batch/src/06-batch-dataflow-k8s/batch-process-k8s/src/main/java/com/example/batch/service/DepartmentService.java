package com.example.batch.service;

import com.example.batch.model.Department;

import java.util.Optional;

public interface DepartmentService {
   Optional<Department> getById(int id);
}
