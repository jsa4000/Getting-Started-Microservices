package com.example.hotel.service.impl;

import com.example.hotel.domain.Booking;
import com.example.hotel.exception.BookingNotFoundException;
import com.example.hotel.exception.BookingServiceException;
import com.example.hotel.repositories.BookingRepository;
import com.example.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;

    @Override
    public Mono<Booking> save(Booking booking) {
        return bookingRepository.findById(booking.getId())
                        .flatMap(x -> bookingRepository.save(booking))
                        .switchIfEmpty(Mono.defer(() -> bookingRepository.save(booking)))
                        .flatMap(x -> bookingRepository.findById(booking.getId()))
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
