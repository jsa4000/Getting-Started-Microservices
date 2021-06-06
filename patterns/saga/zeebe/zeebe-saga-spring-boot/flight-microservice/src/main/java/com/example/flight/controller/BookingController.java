package com.example.flight.controller;

import com.example.flight.domain.Booking;
import com.example.flight.exception.BookingNotFoundException;
import com.example.flight.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/")
    public Mono<ResponseEntity<Booking>> save(@RequestBody Booking booking) {
        return bookingService.save(booking)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @DeleteMapping("/booking/{id}")
    public Mono<ResponseEntity<Void>> deleteBooking(@PathVariable("id") String id) {
        return bookingService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping("/booking/{id}")
    public Mono<ResponseEntity<Booking>> findBookingById(@PathVariable("id") String id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping("/booking/")
    public Mono<ResponseEntity<Flux<Booking>>> findAllBookings() {
        return Mono.just(ResponseEntity.ok(bookingService.findAll()
                .onErrorMap(this::handleError)));
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof BookingNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
