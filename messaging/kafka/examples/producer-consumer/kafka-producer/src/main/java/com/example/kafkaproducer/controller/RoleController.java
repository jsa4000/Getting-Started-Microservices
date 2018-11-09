package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestBody Role role){
        return ResponseEntity.ok("{ transaction_id: uuid_create_transaction }");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id){
        return ResponseEntity.ok("{ transaction_id: uuid_delete_transaction }");
    }

}