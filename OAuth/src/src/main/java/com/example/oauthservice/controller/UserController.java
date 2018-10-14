package com.example.oauthservice.controller;

import com.example.oauthservice.model.User;
import com.example.oauthservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List listUsers(){
        return userService.findAll();
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user){
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        userService.delete(id);
        return ResponseEntity.ok("success");
    }

}