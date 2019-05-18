package com.example.ecommerce.pet.repository;

import com.example.ecommerce.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Pet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

}
