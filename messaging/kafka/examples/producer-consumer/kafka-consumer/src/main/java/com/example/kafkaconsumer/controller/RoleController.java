package com.example.kafkaconsumer.controller;

import com.example.kafkaconsumer.model.Role;
import com.example.kafkaconsumer.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags="Roles", description = "Roles View.")
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "Get a List of Roles")
    @GetMapping("/")
    public ResponseEntity<List> listRoles(){
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable(value = "id") String id){
        Role role = roleService.findById(id);
        if (role == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.ok(role);
    }

}