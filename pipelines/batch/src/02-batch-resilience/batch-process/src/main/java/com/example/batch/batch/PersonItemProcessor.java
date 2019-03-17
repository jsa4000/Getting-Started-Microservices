package com.example.batch.batch;

import com.example.batch.model.Department;
import com.example.batch.model.Person;
import com.example.batch.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Optional;

@Slf4j
@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Autowired
    DepartmentService deparmentService;

    @Override
    public Person process(final Person person) {
        Optional<Department> dep = deparmentService.getById(person.getDepartmentId());
        if (dep.isPresent()) {
            log.info("From Rest Service; " + dep.get().getName());
        } else {
            dep = Optional.of(new Department(0, "Error retrieving the department"));
        }
        final Person transformedPerson = new Person(person.getFirstName().toUpperCase(),
                person.getLastName().toUpperCase(),
                dep.get().getName(),
                Calendar.getInstance().getTime());
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }

}
