package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Page<Customer> findAll(Pageable pageable) { return repository.findAll(pageable); }

    public Customer create(Customer customer) {return  repository.save(customer); }

    public void delete(String id) {repository.deleteById(id);}

}
