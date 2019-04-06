package com.example.generator.mapper;

import com.example.generator.model.PersonUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
public class RecordFieldSetMapper implements FieldSetMapper<PersonUnitTest> {

    public PersonUnitTest mapFieldSet(FieldSet fieldSet) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        PersonUnitTest person = new PersonUnitTest();

        person.setId(fieldSet.readString("id"));
        person.setFirstName(fieldSet.readString("firstName"));
        person.setLastName(fieldSet.readString("lastName"));
        String dateString = fieldSet.readString("birth");
        try {
            person.setBirth(dateFormat.parse(dateString));
        } catch (ParseException ex) {
            log.error("Error parsing the date", ex);
        }
        return person;
    }

}
