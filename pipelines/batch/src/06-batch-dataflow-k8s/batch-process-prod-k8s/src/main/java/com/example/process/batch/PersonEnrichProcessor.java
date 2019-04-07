package com.example.process.batch;

import com.example.process.model.Department;
import com.example.process.model.Person;
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
public class PersonEnrichProcessor implements ItemProcessor<Person, Person> {

    static Random random = new Random();

    @Autowired
    DepartmentService departmentService;

    @Recover
    public Person fallback(final Person person) {
        person.setGroupName("Fatal Error retrieving the department");
        person.setUpdateTime(Calendar.getInstance().getTime());
        log.info("From Fallback, converting (" + person + ") into (" + person + ")");
        return person;
    }

    @Override
    @CircuitBreaker(maxAttempts = 2, resetTimeout= 20000, openTimeout = 5000)
    public Person process(final Person person) throws Exception {
        // To-Do: temporary randomize the department
        person.setDepartment(random.nextInt(10));
        Optional<Department> dep = departmentService.getById(person.getDepartment());
        if (dep.isPresent()) {
             if (dep.get().getName().toUpperCase().equals("UNKNOWN")) {
                 log.info("Filtering (" + person + ")");
                return null;
            }
            log.info("From Rest Service; " + dep.get().getName());
        } else {
            dep = Optional.of(new Department(0, "Error retrieving the department"));
        }

        person.setGroupName(dep.get().getName());
        person.setUpdateTime(Calendar.getInstance().getTime());
        log.info("Converting (" + person + ") into (" + person + ")");
        return person;
    }

}
