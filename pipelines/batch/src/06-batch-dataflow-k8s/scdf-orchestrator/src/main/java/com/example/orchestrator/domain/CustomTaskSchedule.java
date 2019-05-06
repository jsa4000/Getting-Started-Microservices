package com.example.orchestrator.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name="custom_task_schedule",
        indexes = { @Index(name = "idx_cts_ns", columnList = "name,status")})
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

    @Column(name = "created_time", nullable=false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_time", nullable=false)
    private LocalDateTime updatedOn;

    @Column(name = "status", nullable=false, length=256)
    private String status;

    @PrePersist
    public void prePersist() { createdOn = updatedOn = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() {
        updatedOn = LocalDateTime.now();
    }

}
