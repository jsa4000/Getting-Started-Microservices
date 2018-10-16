package com.example.oauthservice.service;

import com.example.oauthservice.model.Role;
import com.example.oauthservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "roleService")
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public List<Role> findAll() { return repository.findAll(); }

    public Role findById(String id) { return repository.findById(id).get(); }

    public Role save(Role role){
        Role exitingRole = repository.findByName(role.getName());
        if (exitingRole != null){
            role.setId(exitingRole.getId());
        }
        return repository.save(role);
    }

    public void delete(String id){ repository.deleteById(id); }
}
