package com.example.generator;

import com.example.generator.mapper.RecordFieldSetMapper;
import com.example.generator.model.CustomerUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(Parameterized.class)
public class CustomerReaderTest {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    @Parameterized.Parameter
    public String Name;

    @Parameterized.Parameter(1)
    public String line;

    @Parameterized.Parameter(2)
    public CustomerUnitTest expectedCustomer;

    @Parameterized.Parameter(3)
    public Class expectedException;

    @Parameterized.Parameters(name = "{index}: {0} - line({1})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "All Fields Valid",
                        "07cb9807-8401-42e1-94aa-4d8423278503,Eldridge,Murazik,Mose Mante,Senior Quality Designer,dewayne.mcglynn@hotmail.com,747.431.2346,1984-48-20 09:48:04,\"Suite 921 26554 Greenfelder Island, Tonettemouth, RI 28215\",Sarita Square,South Mikafurt,23792,Ethiopia,Connecticut,\"Fahey, Klein and Bradtke\",966-82-9132,Mining Orchestrator,2,2018-03-28 09:03:32,2019-00-06 03:00:55",
                        CustomerUnitTest.builder()
                                .id("07cb9807-8401-42e1-94aa-4d8423278503")
                                .firstName("Eldridge")
                                .lastName("Murazik")
                                .fullName("Mose Mante")
                                .title("Senior Quality Designer")
                                .email("dewayne.mcglynn@hotmail.com")
                                .phone("747.431.2346")
                                .birth(parseDate("1984-48-20 09:48:04"))
                                .address("Suite 921 26554 Greenfelder Island, Tonettemouth, RI 28215")
                                .street("Sarita Square")
                                .city("South Mikafurt")
                                .zipCode("23792")
                                .country("Ethiopia")
                                .state("Connecticut")
                                .company("Fahey, Klein and Bradtke")
                                .creditCardNumber("966-82-9132")
                                .jobTitle("Mining Orchestrator")
                                .department(2)
                                .startDate(parseDate("2018-03-28 09:03:32"))
                                .endDate(parseDate("2019-00-06 03:00:55"))
                                .build(),
                        null
                },
                {
                        "Mixed columns with commas",
                        "d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a,Jack,Marquardt,Hae Goyette Jr.,Direct Group Administrator,cesar.oconner@hotmail.com,666-085-8946,2006-36-09 04:36:03,\"Suite 746 103 Batz Prairie, Port Raymundo, AL 93784-6906\",Gerhold Isle,West Enriquetashire,12930-9356,India,Texas,White-Blanda,984-30-1288,Mining Executive,3,2017-56-26 11:56:23,2019-30-05 08:30:52",
                        CustomerUnitTest.builder()
                                .id("d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a")
                                .firstName("Jack")
                                .lastName("Marquardt")
                                .fullName("Hae Goyette Jr.")
                                .title("Direct Group Administrator")
                                .email("cesar.oconner@hotmail.com")
                                .phone("666-085-8946")
                                .birth(parseDate("2006-36-09 04:36:03"))
                                .address("Suite 746 103 Batz Prairie, Port Raymundo, AL 93784-6906")
                                .street("Gerhold Isle")
                                .city("West Enriquetashire")
                                .zipCode("12930-9356")
                                .country("India")
                                .state("Texas")
                                .company("White-Blanda")
                                .creditCardNumber("984-30-1288")
                                .jobTitle("Mining Executive")
                                .department(3)
                                .startDate(parseDate("2017-56-26 11:56:23"))
                                .endDate(parseDate("2019-30-05 08:30:52"))
                                .build(),
                        null
                },
                {
                        "Incorrect number of tokens in record",
                        "b3acf05e-68f8-463d-b0cb-d59c1d37b6fc,Ouida,Murphy,Carola Emard,Dynamic Interactions Designer,maricruz.parisian@hotmail.com,1-476-103-3200,1973-25-21 10:25:51",
                        null,
                        FlatFileParseException.class
                },
                {
                        "Incorrect date format",
                        "d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a,Jack,Marquardt,Hae Goyette Jr.,Direct Group Administrator,cesar.oconner@hotmail.com,666-085-8946,Wrong date!,\"Suite 746 103 Batz Prairie, Port Raymundo, AL 93784-6906\",Gerhold Isle,West Enriquetashire,12930-9356,India,Texas,White-Blanda,984-30-1288,Mining Executive,2,2017-56-26 11:56:23,2019-30-05 08:30:52",
                        null,
                        FlatFileParseException.class
                },
                {
                        "Incorrect integer format",
                        "d45adf26-9ad5-4aa9-8fdc-98cde7d6a70a,Jack,Marquardt,Hae Goyette Jr.,Direct Group Administrator,cesar.oconner@hotmail.com,666-085-8946,Wrong date!,\"Suite 746 103 Batz Prairie, Port Raymundo, AL 93784-6906\",Gerhold Isle,West Enriquetashire,12930-9356,India,Texas,White-Blanda,984-30-1288,Mining Executive,Non Integer!,2017-56-26 11:56:23,2019-30-05 08:30:52",
                        null,
                        FlatFileParseException.class
                }
        });
    }

    @Test
    public void testSimpleFixedLength() throws Exception {
        FlatFileItemReader<CustomerUnitTest> reader = new FlatFileItemReaderBuilder<CustomerUnitTest>()
                .name("CustomerReader")
                .resource(getResource(line))
                .delimited()
                .names(new String[]{
                        "id",
                        "firstName",
                        "lastName",
                        "fullName",
                        "title",
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
                        "department",
                        "startDate",
                        "endDate"})
                .fieldSetMapper(new RecordFieldSetMapper())
                .build();
        reader.open(new ExecutionContext());

        if (expectedException == null) {
            CustomerUnitTest item = reader.read();
            assertThat(item).isEqualToComparingFieldByField(expectedCustomer);
            assertNull(reader.read());
        } else {
            assertThrows(expectedException, reader::read);
        }

    }

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception ex) {
            return new Date();
        }

    }

    private Resource getResource(String contents) {
        return new ByteArrayResource(contents.getBytes());
    }
}
