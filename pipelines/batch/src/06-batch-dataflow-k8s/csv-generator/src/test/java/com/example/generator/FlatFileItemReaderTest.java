package com.example.generator;

import com.example.generator.model.PersonTest;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FlatFileItemReaderTest {


    @Test
    public void testSimpleFixedLength() throws Exception {
        FlatFileItemReader<PersonTest> reader = new FlatFileItemReaderBuilder<PersonTest>()
                .name("personReader")
                .resource(getResource("1234,John,Mc Gregor"))
                .delimited()
                .names(new String[]{"id","firstName", "lastName"})
                .targetType(PersonTest.class)
                .build();

        reader.open(new ExecutionContext());
        PersonTest item = reader.read();
        assertEquals("1234", item.getId());
        assertEquals("John", item.getFirstName());
        assertEquals("Mc Gregor", item.getLastName());
        assertNull(reader.read());
    }

    private Resource getResource(String contents) {
        return new ByteArrayResource(contents.getBytes());
    }
}
