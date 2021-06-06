package com.example.zeebe.task;

import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;

public interface ZeebeTask {

    void process(final JobClient client, final ActivatedJob job) throws Throwable;

    default void rollback(final JobClient client, final ActivatedJob job) throws Throwable {}

}
