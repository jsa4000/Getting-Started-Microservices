package com.example.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("booking")
public class Booking {

    @Id
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
