package com.example.management.service;

import com.example.management.model.Role;

import java.util.List;
import java.util.Optional;


public interface RoleService {

    List<Role> findAll();

    Optional<Role> findById(String id) ;

    Role save(Role role);

    void delete(String id);
}
