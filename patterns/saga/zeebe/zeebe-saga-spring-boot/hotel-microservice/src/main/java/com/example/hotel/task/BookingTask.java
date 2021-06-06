package com.example.hotel.task;

import com.example.hotel.domain.Booking;
import com.example.hotel.service.BookingService;
import com.example.hotel.task.message.BookingData;
import com.example.zeebe.log.LogTask;
import com.example.zeebe.task.ResponseTask;
import com.example.zeebe.task.ZeebeTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static com.example.zeebe.task.TaskError.INPUT_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingTask implements ZeebeTask {

    static final String TASK_NAME = "hotel-booking";
    static final String REQUEST_NAME_HEADER = "requestName";
    static final String RESULT_NAME_HEADER = "resultName";
    static final String SIMULATE_ERROR_HEADER = "simulateError";

    public final ObjectMapper objectMapper;

    public final BookingService bookingService;

    @ZeebeWorker(type = TASK_NAME)
    public void process(final JobClient client, final ActivatedJob job) throws Throwable{
        LogTask.logJob(job);

        String requestName = job.getCustomHeaders().get(REQUEST_NAME_HEADER);
        String resultName = job.getCustomHeaders().get(RESULT_NAME_HEADER);
        Boolean simulateError = Boolean.parseBoolean(job.getCustomHeaders().get(SIMULATE_ERROR_HEADER));

        if (!job.getVariablesAsMap().containsKey(requestName) || simulateError) {
            complete(client, job, resultName, ResponseTask.ko(INPUT_ERROR).serialize(objectMapper));
            return;
        }

        ResponseTask<BookingData> booking = ResponseTask.deserialize(job.getVariablesAsMap().get(requestName), objectMapper, BookingData.class);
        // TODO: To Check booking dates, if there is already a booking on that interval

        Booking bookingData = Booking.builder()
                .id(UUID.randomUUID().toString())
            .clientId(booking.getData().getClientId())
            .resourceId("123")
            .fromDate(booking.getData().getFromDate())
            .toDate(booking.getData().getToDate())
            .createdAt(booking.getData().getCreatedAt())
            .active(booking.getData().getActive())
            .build();

        Map<String, Object> response = ResponseTask.ok(bookingData).serialize(objectMapper);
        bookingService.save(bookingData)
                .subscribe(x -> complete(client, job, resultName, response));
    }

    @ZeebeWorker(type = TASK_NAME + "-rollback")
    public void rollback(final JobClient client, final ActivatedJob job) throws Throwable{
        LogTask.logJob(job);

        String resultName = job.getCustomHeaders().get(RESULT_NAME_HEADER);

        if (!job.getVariablesAsMap().containsKey(resultName)) {
            client.newCompleteCommand(job.getKey()).send().join();
            return;
        }

        ResponseTask<BookingData> booking = ResponseTask
                .deserialize(job.getVariablesAsMap().get(resultName), objectMapper, BookingData.class);
        if (!booking.getSuccess()) {
            client.newCompleteCommand(job.getKey()).send().join();
            return;
        }

        bookingService.delete(booking.getData().getId())
                .then(Mono.just(client.newCompleteCommand(job.getKey()).send().join()))
                .subscribe();
    }

    private Mono<Void> complete(final JobClient client, final ActivatedJob job, final String key, final Map value) {
        client.newCompleteCommand(job.getKey()).variables(Map.of(key, value)).send().join();
        return Mono.empty();
    }

}
