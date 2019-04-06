package com.example.generator;

import com.example.generator.mapper.RecordFieldSetMapper;
import com.example.generator.model.PersonBuilder;
import com.example.generator.model.PersonUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class PersonReaderTest {

    @Parameterized.Parameter
    public String line;

    @Parameterized.Parameter(1)
    public PersonBuilder expectedPerson;

    @Parameterized.Parameters(name = "{index}: line({0})={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "07cb9807-8401-42e1-94aa-4d8423278503,Eldridge,Murazik,Mose Mante,Senior Quality Designer,dewayne.mcglynn@hotmail.com,747.431.2346,1984-48-20 09:48:04,\"Suite 921 26554 Greenfelder Island, Tonettemouth, RI 28215\",Sarita Square,South Mikafurt,23792,Ethiopia,Connecticut,\"Fahey, Klein and Bradtke\",966-82-9132,Mining Orchestrator,2018-03-28 09:03:32,2019-00-06 03:00:55", PersonBuilder.builder()
                        .id("07cb9807-8401-42e1-94aa-4d8423278503")
                        .firstName("Eldridge")
                        .lastName("Murazik")
                        .build() },
                { "d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a,Jack,Marquardt,Hae Goyette Jr.,Direct Group Administrator,cesar.oconner@hotmail.com,666-085-8946,2006-36-09 04:36:03,\"Suite 746 103 Batz Prairie, Port Raymundo, AL 93784-6906\",Gerhold Isle,West Enriquetashire,12930-9356,India,Texas,White-Blanda,984-30-1288,Mining Executive,2017-56-26 11:56:23,2019-30-05 08:30:52", PersonBuilder.builder()
                        .id("d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a")
                        .firstName("Jack")
                        .lastName("Marquardt")
                        .build() },
                { "b3acf05e-68f8-463d-b0cb-d59c1d37b6fc,Ouida,Murphy,Carola Emard,Dynamic Interactions Designer,maricruz.parisian@hotmail.com,1-476-103-3200,1973-25-21 10:25:51,\"Suite 296 70286 Rice Parkways, West Julietaborough, CO 02180-0907\",Kihn Extension,Haaghaven,10155,Slovakia (Slovak Republic),Arizona,Haley-Kub,972-31-7969,Consulting Officer,2019-28-24 06:28:17,2019-58-05 11:58:34", PersonBuilder.builder()
                        .id("b3acf05e-68f8-463d-b0cb-d59c1d37b6fc")
                        .firstName("Ouida")
                        .lastName("Murphy")
                        .build() }
        });
    }

    @Test
    public void testSimpleFixedLength() throws Exception {

        /*
        DefaultFieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
        fieldSetFactory.setDateFormat(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"));
        */

        FlatFileItemReader<PersonUnitTest> reader = new FlatFileItemReaderBuilder<PersonUnitTest>()
                .name("personReader")
                .resource(getResource(line))
                .delimited()
                //.fieldSetFactory(fieldSetFactory)
                .names(new String[]{
                        "id",
                        "firstName",
                        "lastName",
                        "fullName",
                        "gender",
                        "email",
                        "phone",
                        "birth",
                        "address",
                        "street",
                        "city",
                        "zipCode",
                        "country",
                        "state",
                        "company",
                        "creditCardNumber",
                        "jobTitle",
                        "startDate",
                        "endDate"})
                .fieldSetMapper(new RecordFieldSetMapper())
                //.targetType(PersonUnitTest.class) // If atuomatic mapper
                .build();

        reader.open(new ExecutionContext());
        PersonUnitTest item = reader.read();
        //assertThat(item).isEqualToComparingFieldByField(expectedPerson);
        assertEquals(expectedPerson.getId(), item.getId());
        assertEquals(expectedPerson.getFirstName(), item.getFirstName());
        assertNull(reader.read());
    }

    private Resource getResource(String contents) {
        return new ByteArrayResource(contents.getBytes());
    }
}
