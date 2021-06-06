package com.example.flight.task.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingData {

    String id;
    String clientId;
    String houseBookingId;
    String carBookingId;
    String flightBookingId;
    OffsetDateTime fromDate;
    OffsetDateTime toDate;
    OffsetDateTime createdAt;
    Boolean active;

}
