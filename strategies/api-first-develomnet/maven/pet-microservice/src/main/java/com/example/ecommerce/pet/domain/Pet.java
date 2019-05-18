package com.example.ecommerce.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Pet.
 */
@Entity
@Data
@Builder
@Table(name = "pet")
@NoArgsConstructor
@AllArgsConstructor
public class Pet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @NotNull
    @Size(max = 256)
    @Column(name = "tag", length = 256, nullable = false)
    private String tag;

}
