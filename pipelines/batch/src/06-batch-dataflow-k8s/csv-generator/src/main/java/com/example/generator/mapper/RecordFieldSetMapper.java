package com.example.generator.mapper;

import com.example.generator.model.PersonUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class RecordFieldSetMapper implements FieldSetMapper<PersonUnitTest> {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public PersonUnitTest mapFieldSet(FieldSet fieldSet) {
        return PersonUnitTest.builder()
                .id(fieldSet.readString("id"))
                .firstName(fieldSet.readString("firstName"))
                .lastName(fieldSet.readString("lastName"))
                .birth(parseDate(fieldSet.readString("birth")))
                .build();
    }

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception ex) {
            return new Date();
        }

    }

}
