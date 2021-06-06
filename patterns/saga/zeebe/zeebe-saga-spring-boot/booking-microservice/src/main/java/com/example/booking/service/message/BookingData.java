package com.example.booking.service.message;

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
    String resourceId;
    OffsetDateTime fromDate;
    OffsetDateTime toDate;
    OffsetDateTime createdAt;
    Boolean active;

}
