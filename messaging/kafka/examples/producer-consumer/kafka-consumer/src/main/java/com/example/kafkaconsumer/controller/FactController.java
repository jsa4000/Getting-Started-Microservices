package com.example.kafkaconsumer.controller;

import com.example.kafkaconsumer.model.Fact;
import com.example.kafkaconsumer.service.FactService;
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

@Api(tags="Facts", description = "Facts View.")
@RestController
@RequestMapping("/facts")
public class FactController {

    @Autowired
    private FactService factService;

    @ApiOperation(value = "Get a List of Facts")
    @GetMapping("/")
    public ResponseEntity<List> listFacts(){
        return ResponseEntity.ok(factService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fact> getFact(@PathVariable(value = "id") String id){
        Fact fact = factService.findById(id);
        if (fact == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.ok(fact);
    }

}