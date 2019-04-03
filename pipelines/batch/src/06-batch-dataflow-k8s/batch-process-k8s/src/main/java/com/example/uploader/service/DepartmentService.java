package com.example.uploader.service;

import com.example.uploader.model.Department;

import java.util.Optional;

public interface DepartmentService {
   Optional<Department> getById(int id);
}
