package com.example.hotel.repositories;

import com.example.hotel.domain.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {

    /**
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    Flux<Booking> findByFromDateGreaterThanEqualAndToDateLessThanEqual(OffsetDateTime fromDate, OffsetDateTime toDate);

}
