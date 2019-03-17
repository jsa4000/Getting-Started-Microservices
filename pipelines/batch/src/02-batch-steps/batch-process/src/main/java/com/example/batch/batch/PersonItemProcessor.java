package com.example.batch.batch;

import com.example.batch.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Slf4j
@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(final Person person) throws Exception {
        final Person transformedPerson = new Person(person.getFirstName().toUpperCase(),
                person.getLastName().toUpperCase(),
                "Empty",
                Calendar.getInstance().getTime());
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }

}
