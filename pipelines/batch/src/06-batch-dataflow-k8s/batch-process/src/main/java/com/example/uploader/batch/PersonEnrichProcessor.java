package com.example.uploader.batch;

import com.example.uploader.model.Department;
import com.example.uploader.model.Person;
import com.example.uploader.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Optional;

@Slf4j
@Profile("worker")
@Component
public class PersonEnrichProcessor implements ItemProcessor<Person, Person> {

    @Autowired
    DepartmentService departmentService;

    @Recover
    public Person fallback(final Person person) {
        final Person transformedPerson = new Person(person.getFirstName().toUpperCase(),
                person.getLastName().toUpperCase(),
                "Fatal Error retrieving the department",
                person.getGroupName(),
                Calendar.getInstance().getTime());
        log.info("From Fallback, converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }

    @Override
    @CircuitBreaker(maxAttempts = 2, resetTimeout= 20000, openTimeout = 5000)
    public Person process(final Person person) throws Exception {
        Optional<Department> dep = departmentService.getById(person.getDepartmentId());
        if (dep.isPresent()) {
             if (dep.get().getName().toUpperCase().equals("UNKNOWN")) {
                 log.info("Filtering (" + person + ")");
                return null;
            }
            log.info("From Rest Service; " + dep.get().getName());
        } else {
            dep = Optional.of(new Department(0, "Error retrieving the department"));
        }
        final Person transformedPerson = new Person(person.getFirstName().toUpperCase(),
                person.getLastName().toUpperCase(),
                dep.get().getName(),
                person.getGroupName(),
                Calendar.getInstance().getTime());
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }

}
