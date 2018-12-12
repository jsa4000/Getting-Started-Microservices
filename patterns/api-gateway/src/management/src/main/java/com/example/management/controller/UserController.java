package com.example.management.controller;

import com.example.management.exception.UserNotFoundException;
import com.example.management.model.User;
import com.example.management.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List> getUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    @ApiOperation(value = "Get user by Id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") String id){
        Optional<User> user = userService.findById(id);
        if (!user.isPresent())
            throw new UserNotFoundException("User not found");
        return ResponseEntity.ok(user.get());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ApiOperation(value = "Create or update an user")
    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ApiOperation(value = "Delete an existing user")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        userService.delete(id);
        return ResponseEntity.ok("success");
    }

}