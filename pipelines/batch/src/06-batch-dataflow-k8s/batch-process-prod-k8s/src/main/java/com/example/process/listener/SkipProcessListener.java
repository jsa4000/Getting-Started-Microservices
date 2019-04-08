package com.example.process.listener;

import com.example.process.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("worker")
public class SkipProcessListener implements SkipListener<Customer, Customer> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.info("Skipped Record Read");
    }

    @Override
    public void onSkipInWrite(Customer item, Throwable t) {
        log.info("Skipped Record Written (" + item + ")");
    }

    @Override
    public void onSkipInProcess(Customer item, Throwable t) {
        log.info("Skipped Record Processed(" + item + ")");
    }
}
