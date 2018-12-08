package com.example.management.controller;

import com.example.management.exception.RoleNotFoundException;
import com.example.management.model.Role;
import com.example.management.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @ApiOperation(value = "Get role by Id")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable(value = "id") String id){
        Optional<Role> role = roleService.findById(id);
        if (!role.isPresent())
            throw new RoleNotFoundException("Role not found");
        return ResponseEntity.ok(role.get());
    }

    @ApiOperation(value = "Create or update a role")
    @PostMapping("/")
    public ResponseEntity<Role> createUser(@RequestBody Role role){
        return ResponseEntity.ok(roleService.save(role));
    }

    @ApiOperation(value = "Delete an existing role")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        roleService.delete(id);
        return ResponseEntity.ok("success");
    }

}