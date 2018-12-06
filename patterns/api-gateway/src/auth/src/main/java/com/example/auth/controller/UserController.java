package com.example.auth.controller;

import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.User;
import com.example.auth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(tags="Users", description = "Users View.")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Get a List of Users")
    @GetMapping("/")
    public ResponseEntity<List> listUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    @ApiOperation(value = "Get user by Id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") String id){
        Optional<User> user = userService.findById(id);
        if (!user.isPresent())
            throw new UserNotFoundException("User not found");
        return ResponseEntity.ok(user.get());
    }

    @ApiOperation(value = "Create or update an user")
    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }

    @ApiOperation(value = "Delete an existing user")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        userService.delete(id);
        return ResponseEntity.ok("success");
    }

}