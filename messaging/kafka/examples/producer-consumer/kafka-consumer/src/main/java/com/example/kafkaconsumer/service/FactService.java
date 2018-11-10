package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.Fact;
import com.example.kafkaconsumer.repository.FactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FactService {

    @Autowired
    private FactRepository repository;

     public List<Fact> findAll() { return repository.findAll(); }

    public Fact findById(String id) { return repository.findById(id).get(); }

    public Fact save(Fact fact){ return repository.save(fact); }

}
