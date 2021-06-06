package com.example.hotel.domain;

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
@Document("hotel_booking")
public class Booking {

    @Id
    String id;
    String clientId;
    String resourceId;
    OffsetDateTime fromDate;
    OffsetDateTime toDate;
    OffsetDateTime createdAt;
    Boolean active;

}
