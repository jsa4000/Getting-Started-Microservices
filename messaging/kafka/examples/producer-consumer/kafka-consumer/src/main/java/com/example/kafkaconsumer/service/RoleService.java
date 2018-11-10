package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.Role;
import com.example.kafkaconsumer.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public List<Role> findAll() {
        return repository.findAll();
    }

    public Role findById(String id) {
        return repository.findById(id).get();
    }

    public Role save(Role role){
        Optional<Role> exitingRole = repository.findById(role.getId());
        if (exitingRole.isPresent()){
            role.setId(exitingRole.get().getId());
        }
        return repository.save(role);
    }

    public void delete(String id){ repository.deleteById(id); }
}
