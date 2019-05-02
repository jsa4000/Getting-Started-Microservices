package com.example.petstore.service;

import com.example.petstore.mapper.PetMapper;
import com.example.petstore.web.api.PetsApiDelegate;
import com.example.petstore.web.api.model.NewPetDto;
import com.example.petstore.web.api.model.PetDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        // Call to Pets service
        com.example.petstore.domain.Pet pet = petService.save(petMapper.toDomain(newPet));
        return ResponseEntity.ok(petMapper.fromDomain(pet));
    }

    @Override
    public ResponseEntity<Void> deletePet(Long id) {
        petService.delete(id);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<PetDto> findPetById(Long id) {
        // Call to Pets service
        Optional<com.example.petstore.domain.Pet> pet = petService.findOne(id);

        if (!pet.isPresent()) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(petMapper.fromDomain(pet.get()));
    }

    @Override
    public ResponseEntity<List<PetDto>> findPets(List<String> tags, Integer limit) {
        Pageable pages = PageRequest.of(0, limit);
        List<PetDto> result = petService.findAll(pages)
            .getContent().stream()
            .map(petMapper::fromDomain)
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
