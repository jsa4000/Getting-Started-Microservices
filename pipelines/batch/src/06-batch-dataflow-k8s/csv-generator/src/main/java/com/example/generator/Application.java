package com.example.generator;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void writeCsvFromBean(Path path, long count) throws Exception {
        Writer writer  = new FileWriter(path.toString());

        StatefulBeanToCsv<PersonBean> sbc = new StatefulBeanToCsvBuilder<PersonBean>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();

        for (int i = 0; i < count; i++) {
            PersonBean person = new PersonBean();
            sbc.write(person);
            i++;
        }
        writer.close();
    }

    public void writeCsv(Path path, long count) throws Exception {
        Writer outputFile  = new FileWriter(path.toString());
        CSVWriter writer = new CSVWriter(outputFile);
        for (int i = 0; i < count; i++) {
            Rower item = new Person();
            writer.writeNext(item.row());
        }
        writer.close();
    }

    @Override
    public void run(String... args) {
        log.info("Starting CSV Generator");

        String pathString = "/tmp/bigfile.csv";
        long count = 100000;

        long startTime = System.nanoTime();

        try {
            writeCsv(Paths.get(pathString),count);
        } catch (Exception ex) {
           log.error("Error during the generation of the csv file",ex);
        }

        long duration = (System.nanoTime() - startTime) / 1000000;

        log.info("CSV Finished in " + duration + "ms");
    }
}
