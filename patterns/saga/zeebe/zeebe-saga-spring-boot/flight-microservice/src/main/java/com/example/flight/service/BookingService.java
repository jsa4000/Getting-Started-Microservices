package com.example.flight.service;

import com.example.flight.domain.Booking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    /**
     * Create or Update a booking
     *
     * @param booking
     * @return
     */
    Mono<Booking> save(Booking booking);

    /**
     * Delete an existing Booking
     *
     * @param id
     * @return
     */
    Mono<Void> delete(String id);

    /**
     * Retrieve all booking
     *
     * @return
     */
    Flux<Booking> findAll();

    /**
     * Find booking by Id
     *
     * @param id
     * @return
     */
     Mono<Booking> findById(String id);
}
