package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.model.Role;
import com.example.kafkaproducer.service.RoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags="Roles", description = "Roles Management.")
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/")
    public ResponseEntity<String> createRole(@RequestBody Role role){
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PatchMapping("/")
    public ResponseEntity<String> modifyRole(@RequestBody Role role){
        return ResponseEntity.ok(roleService.modifyRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable(value = "id") String id){
        return ResponseEntity.ok(roleService.deleteRole(id));
    }

}