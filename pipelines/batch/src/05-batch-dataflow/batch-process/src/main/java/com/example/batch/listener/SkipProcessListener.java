package com.example.batch.listener;

import com.example.batch.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkipProcessListener implements SkipListener<Person, Person> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.info("Skipped Record Read");
    }

    @Override
    public void onSkipInWrite(Person item, Throwable t) {
        log.info("Skipped Record Written (" + item + ")");
    }

    @Override
    public void onSkipInProcess(Person item, Throwable t) {
        log.info("Skipped Record Processed(" + item + ")");
    }
}
