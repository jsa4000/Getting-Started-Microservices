package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository repository;

    @Override
    public Page<Customer> findAll(Pageable pageable) { return repository.findAll(pageable); }

    @Override
    public Customer create(Customer customer) {return  repository.save(customer); }

    @Override
    public void delete(String id) {repository.deleteById(id);}

    @Override
    public Optional<Customer> getCustomerById(String id) {return repository.findById(id); }

}
