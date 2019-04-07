package com.example.process.batch;

import com.example.process.model.Department;
import com.example.process.model.Customer;
import com.example.process.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Profile("worker")
@Component
public class PersonEnrichProcessor implements ItemProcessor<Customer, Customer> {

    static Random random = new Random();

    @Autowired
    DepartmentService departmentService;

    @Recover
    public Customer fallback(final Customer customer) {
        customer.setGroupName("Fatal Error retrieving the department");
        customer.setUpdateTime(Calendar.getInstance().getTime());
        log.info("From Fallback, converting (" + customer + ") into (" + customer + ")");
        return customer;
    }

    @Override
    @CircuitBreaker(maxAttempts = 2, resetTimeout= 20000, openTimeout = 5000)
    public Customer process(final Customer customer) throws Exception {
        Optional<Department> dep = departmentService.getById(customer.getDepartment());
        if (dep.isPresent()) {
             if (dep.get().getName().toUpperCase().equals("UNKNOWN")) {
                 log.info("Filtering (" + customer + ")");
                return null;
            }
            log.info("From Rest Service; " + dep.get().getName());
        } else {
            dep = Optional.of(new Department(0, "Error retrieving the department"));
        }
        customer.setGroupName(dep.get().getName());
        customer.setUpdateTime(Calendar.getInstance().getTime());
        log.info("Converting (" + customer + ") into (" + customer + ")");
        return customer;
    }

}
