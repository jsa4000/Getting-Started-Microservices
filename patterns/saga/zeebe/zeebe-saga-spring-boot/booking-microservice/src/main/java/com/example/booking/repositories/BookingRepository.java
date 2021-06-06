package com.example.booking.repositories;

import com.example.booking.domain.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
}
