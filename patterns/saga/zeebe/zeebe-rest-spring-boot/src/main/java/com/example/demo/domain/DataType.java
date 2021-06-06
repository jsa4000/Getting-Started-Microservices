package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataType {

    String name;
    String type;
    String description;
    OffsetDateTime createdAt;
    Boolean enabled;
    Integer versionNumber;

}
