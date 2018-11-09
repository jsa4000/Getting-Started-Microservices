package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestBody User user){
        return ResponseEntity.ok("{ transaction_id: uuid_create_transaction }");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        return ResponseEntity.ok("{ transaction_id: uuid_delete_transaction }");
    }

}