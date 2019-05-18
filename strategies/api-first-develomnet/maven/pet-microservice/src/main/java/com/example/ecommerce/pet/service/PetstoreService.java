package com.example.ecommerce.pet.service;

import com.example.ecommerce.pet.domain.Pet;
import com.example.ecommerce.pet.exception.PetNotFoundException;
import com.example.ecommerce.pet.mapper.PetMapper;
import com.example.ecommerce.pet.web.api.PetsApiDelegate;
import com.example.ecommerce.pet.web.api.model.NewPetDto;
import com.example.ecommerce.pet.web.api.model.PetDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetstoreService implements PetsApiDelegate{

    private final PetService petService;

    private final PetMapper petMapper;

    public PetstoreService(PetService petService, PetMapper petMapper) {
        this.petService = petService;
        this.petMapper = petMapper;
    }

    @Override
    public ResponseEntity<PetDto> addPet(NewPetDto newPet) {
        Pet pet = petService.save(petMapper.toDomain(newPet));
        return ResponseEntity.ok(petMapper.fromDomain(pet));
    }

    @Override
    public ResponseEntity<Void> deletePet(Long id) {
        petService.delete(id);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<PetDto> findPetById(Long id) {
        Optional<Pet> pet = petService.findOne(id);

        if (!pet.isPresent()) {
            throw new PetNotFoundException(String.format("Pet id=%s has not been found", id));
        }

        return ResponseEntity.ok(petMapper.fromDomain(pet.get()));
    }

    @Override
    public ResponseEntity<List<PetDto>> findPets(List<String> tags, Integer limit) throws Exception{
        Pageable pages = PageRequest.of(0, limit);
        List<PetDto> result = petService.findAll(pages)
            .getContent().stream()
            .map(petMapper::fromDomain)
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
