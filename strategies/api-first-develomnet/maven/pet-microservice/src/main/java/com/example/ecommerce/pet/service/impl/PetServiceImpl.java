package com.example.ecommerce.pet.service.impl;

import com.example.ecommerce.pet.domain.Pet;
import com.example.ecommerce.pet.repository.PetRepository;
import com.example.ecommerce.pet.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Pet}.
 */
@Service
@Transactional
public class PetServiceImpl implements PetService {

    private final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);

    private final PetRepository petRepository;

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * Save a pet.
     *
     * @param pet the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Pet save(Pet pet) {
        log.debug("Request to save Pet : {}", pet);
        return petRepository.save(pet);
    }

    /**
     * Get all the pets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Pet> findAll(Pageable pageable) {
        log.debug("Request to get all Pets");
        return petRepository.findAll(pageable);
    }


    /**
     * Get one pet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> findOne(Long id) {
        log.debug("Request to get Pet : {}", id);
        return petRepository.findById(id);
    }

    /**
     * Delete the pet by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Pet : {}", id);
        petRepository.deleteById(id);
    }
}
