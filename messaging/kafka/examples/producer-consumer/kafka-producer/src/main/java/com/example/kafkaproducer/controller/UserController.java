package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.model.User;
import com.example.kafkaproducer.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags="Users", description = "Users Management.")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PatchMapping("/")
    public ResponseEntity<String> modifyUser(@RequestBody User user){
        return ResponseEntity.ok(userService.modifyUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }

}