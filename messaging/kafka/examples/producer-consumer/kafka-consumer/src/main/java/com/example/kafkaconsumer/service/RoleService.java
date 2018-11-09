package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.Role;
import com.example.kafkaconsumer.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

    @Cacheable("roles")
    public List<Role> findAll() {
        log.info("Refreshing findAll cache");
        return repository.findAll();
    }

    @Cacheable("roles")
    public Role findById(String id) {
        log.info("Refreshing findById {} cache", id);
        return repository.findById(id).get();
    }

    public Role save(Role role){
        Role exitingRole = repository.findByName(role.getName());
        if (exitingRole != null){
            role.setId(exitingRole.getId());
        }
        return repository.save(role);
    }

    public void delete(String id){ repository.deleteById(id); }
}
