package com.example.process.service;

import com.example.process.model.Department;

import java.util.Optional;

public interface DepartmentService {
   Optional<Department> getById(int id);
}
