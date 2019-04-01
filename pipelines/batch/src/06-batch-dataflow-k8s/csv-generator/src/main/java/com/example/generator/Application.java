package com.example.generator;

import com.example.generator.writer.CsvWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private CsvWriter writer;

    @Value("${pathString:/tmp/bigfile.csv}")
    private String pathString;

    @Value("${count:1000000}")
    private long count;

    public void benchmark(long count) {
        try {
            for (int i = 0; i < 3; i ++) {
                writer.writeOpenCsv(Paths.get("/tmp/bigfile.csv"),count);

                writer.writeCsv(Paths.get("/tmp/bigfile1.csv"),count);

                writer.writeCsvFromBean(Paths.get("/tmp/bigfile2.csv"),count);
            }
        } catch (Throwable ex) {
            log.error("Error during the generation of the csv file",ex);
        }
    }

    @Override
    public void run(String... args){
        log.info("Starting CSV Generator");

        try {
            writer.writeOpenCsv(Paths.get(pathString),count);

        } catch (Throwable ex) {
            log.error("Error during the generation of the csv file",ex);
        }

        log.info("Application Finished");
    }
}
