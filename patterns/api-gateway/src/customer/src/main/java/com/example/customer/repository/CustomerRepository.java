package com.example.customer.repository;

import com.example.customer.model.Address;
import com.example.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.stream.Stream;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {
    List<Customer> findByLastName(String lastName);
    Page<Customer> findByFirstName(String firstName, Pageable pageable);
    Customer findByAddress(Address address);
    Stream<Customer> findAllBy();
}

