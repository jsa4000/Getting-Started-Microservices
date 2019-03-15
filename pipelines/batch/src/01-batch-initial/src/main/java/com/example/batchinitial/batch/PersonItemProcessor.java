package com.example.batchinitial.batch;

import com.example.batchinitial.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Slf4j
@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(final Person person) {
        final Person transformedPerson = new Person(person.getFirstName().toUpperCase(),
                person.getLastName().toUpperCase(),
                Calendar.getInstance().getTime());
        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }

}
