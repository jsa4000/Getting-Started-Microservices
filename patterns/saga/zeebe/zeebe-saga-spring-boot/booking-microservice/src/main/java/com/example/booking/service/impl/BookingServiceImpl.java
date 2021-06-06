package com.example.booking.service.impl;

import com.example.booking.domain.Booking;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.BookingServiceException;
import com.example.booking.repositories.BookingRepository;
import com.example.booking.service.BookingService;
import com.example.booking.service.message.BookingData;
import com.example.zeebe.task.ResponseTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ZeebeClient zeebeClient;

    public final ObjectMapper objectMapper;

    public final BookingRepository bookingRepository;

    @Override
    public Mono<Booking> submit(Booking booking) throws Throwable {
        // TODO: Check if there is a Lock related to the booking to be submitted
        return Mono.fromFuture(zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("trip-booking")
                .latestVersion()
                .variables(Map.of("bookingResult", ResponseTask.ok(booking).serialize(objectMapper)))
                .withResult()
                .requestTimeout(zeebeClient.getConfiguration().getDefaultJobTimeout())
                .send().toCompletableFuture())
                .flatMap(job -> {
                     try {
                         ResponseTask<BookingData> hotelBooking = ResponseTask.deserialize(job.getVariablesAsMap().get("bookHotelResult"), objectMapper, BookingData.class);
                         ResponseTask<BookingData> carBooking = ResponseTask.deserialize(job.getVariablesAsMap().get("bookCarResult"), objectMapper, BookingData.class);
                         ResponseTask<BookingData> flightBooking = ResponseTask.deserialize(job.getVariablesAsMap().get("bookFlightResult"), objectMapper, BookingData.class);
                         booking.setHouseBookingId(hotelBooking.getData().getId());
                         booking.setCarBookingId(carBooking.getData().getId());
                         booking.setFlightBookingId(flightBooking.getData().getId());
                         return bookingRepository.save(booking);
                     } catch (Throwable ex) {
                         return Mono.empty();
                     }
                })
                .onErrorMap(BookingServiceException::new);
    }

    @Override
    public Mono<Void> delete(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()))
                .flatMap(x -> bookingRepository.deleteById(id)
                        .onErrorMap(BookingServiceException::new));
    }

    @Override
    public Flux<Booking> findAll() {
        return bookingRepository.findAll()
                .onErrorMap(BookingServiceException::new);
    }

    @Override
    public Mono<Booking> findById(String id) {
        return bookingRepository.findById(id)
                .onErrorMap(BookingServiceException::new)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()));
    }

}
