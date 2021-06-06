package com.example.flight.repositories;

import com.example.flight.domain.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
}
