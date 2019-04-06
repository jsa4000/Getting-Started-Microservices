package com.example.generator.writer;

import com.example.generator.aop.LogExecutionTime;
import com.example.generator.model.Person;
import com.example.generator.model.PersonBean;
import com.example.generator.model.PersonTest;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class CsvWriter {

    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (Exception ex) {
            log.error("Error deleting the file: ", ex.getMessage());
        }
    }

    @LogExecutionTime
    public void writeCsvFromBean(Path path, long count) throws Throwable {
        deleteFile(path);
        Writer writer  = new FileWriter(path.toString());

        StatefulBeanToCsv<PersonBean> sbc = new StatefulBeanToCsvBuilder<PersonBean>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withApplyQuotesToAll(false)
                .build();

        for (int i = 0; i < count; i++) {
            PersonBean bean = new PersonBean();
            sbc.write(bean);
        }
        writer.close();
    }

    @LogExecutionTime
    public void writeNoRandomCsv(Path path, long count) throws Throwable {
        deleteFile(path);
        Writer outputFile  = new FileWriter(path.toString());
        CSVWriter writer = new CSVWriter(outputFile);
        for (int i = 0; i < count; i++) {
            Rower r = new PersonTest();
            writer.writeNext(r.row(),false);
        }
        writer.close();
    }

    @LogExecutionTime
    public void writeOpenCsv(Path path, long count) throws Throwable {
        deleteFile(path);
        Writer outputFile  = new FileWriter(path.toString());
        CSVWriter writer = new CSVWriter(outputFile);
        for (int i = 0; i < count; i++) {
            Rower r = new Person();
            writer.writeNext(r.row(),false);
        }
        writer.close();
    }

    @LogExecutionTime
    public void writeCsv(Path path, long count) throws Throwable {
        deleteFile(path);
        Writer outputFile  = new FileWriter(path.toString());
        PrintWriter writer = new PrintWriter(outputFile);
        for (int i = 0; i < count; i++) {
            Rower r = new Person();
            writer.println(String.join(",",r.row()));
        }
        writer.close();
    }


}
