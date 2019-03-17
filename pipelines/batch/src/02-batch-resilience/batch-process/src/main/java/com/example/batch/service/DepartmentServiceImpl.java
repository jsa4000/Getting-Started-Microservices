package com.example.batch.service;

import com.example.batch.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DepartmentServiceImpl implements DepartmentService {

    @Value("${app.departmentsUri:http://localhost:8081/departments}")
    String departmentsUri;

    @Autowired
    RestTemplate restTemplate;

    public Optional<Department> getById(int id){
        Map<String, Object> vars = new HashMap<>();
        vars.put("id", id);
        return  Optional.ofNullable(restTemplate.getForObject(departmentsUri + "/{id}",
                Department.class, vars));
    }
}
