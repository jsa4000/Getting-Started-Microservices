package com.example.batchinitial.batch;

import com.example.batchinitial.model.Person;
import com.example.batchinitial.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PersonItemWriter implements ItemWriter<Person> {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void write(List<? extends Person> persons) {
        log.info("Saving into repository. Items: " + persons.size());
        personRepository.saveAll(persons);
    }

}
