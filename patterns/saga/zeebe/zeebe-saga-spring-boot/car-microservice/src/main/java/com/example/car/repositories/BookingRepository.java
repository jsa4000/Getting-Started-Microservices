package com.example.car.repositories;

import com.example.car.domain.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
}
