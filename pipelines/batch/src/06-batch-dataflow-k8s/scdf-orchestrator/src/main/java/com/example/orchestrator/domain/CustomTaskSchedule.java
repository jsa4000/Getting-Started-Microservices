package com.example.orchestrator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name="custom_task_schedule", indexes = { @Index(name = "idx_cts_ns", columnList = "name,status")})
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomTaskSchedule {
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cts_seq_gen")
    //@SequenceGenerator(name = "cts_seq_gen", sequenceName = "cts_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //H2 Support. Not recommended strategy
    private int id;

    @Column(name = "name", nullable=false, length=512)
    private String name;

    @Column(name = "data", length=4096)
    private String data;

    @Column(name = "created_time", nullable=false)
    private LocalDate createdTime;

    @Column(name = "updated_time", nullable=false)
    private LocalDate updatedTime;

    @Column(name = "status", nullable=false, length=256)
    private String status;

}
