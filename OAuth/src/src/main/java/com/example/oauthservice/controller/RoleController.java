package com.example.oauthservice.controller;

import com.example.oauthservice.model.Role;
import com.example.oauthservice.service.RoleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/")
    public ResponseEntity<Role> createUser(@RequestBody Role role){
        return ResponseEntity.ok(roleService.save(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        roleService.delete(id);
        return ResponseEntity.ok("success");
    }

}