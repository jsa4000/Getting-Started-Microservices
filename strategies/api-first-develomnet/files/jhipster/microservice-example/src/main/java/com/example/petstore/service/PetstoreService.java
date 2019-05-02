package com.example.petstore.service;

import com.example.petstore.web.api.PetsApiDelegate;
import com.example.petstore.web.api.model.NewPet;
import com.example.petstore.web.api.model.Pet;
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

    public PetstoreService(PetService petService) {
        this.petService = petService;
    }

    @Override
    public ResponseEntity<Pet> addPet(NewPet newPet) {
        com.example.petstore.domain.Pet pet = new com.example.petstore.domain.Pet();
        pet.setTag(newPet.getTag());
        pet.setName(newPet.getName());

        // Call to Pets service
        pet = petService.save(pet);

        Pet petWeb = new Pet();
        petWeb.setId(pet.getId());
        petWeb.setTag(pet.getTag());
        petWeb.setName(pet.getName());

        return ResponseEntity.ok(petWeb);
    }

    @Override
    public ResponseEntity<Void> deletePet(Long id) {
        petService.delete(id);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Pet> findPetById(Long id) {
        // Call to Pets service
        Optional<com.example.petstore.domain.Pet> pet = petService.findOne(id);

        if (!pet.isPresent()) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Pet petWeb = new Pet();
        petWeb.setId(pet.get().getId());
        petWeb.setTag(pet.get().getTag());
        petWeb.setName(pet.get().getName());

        return ResponseEntity.ok(petWeb);

    }

    @Override
    public ResponseEntity<List<Pet>> findPets(List<String> tags, Integer limit) {
        Pageable pages = PageRequest.of(0, limit);
        List<Pet> result = petService.findAll(pages)
            .getContent().stream()
            .map(pet -> {
                Pet petWeb = new Pet();
                petWeb.setId(pet.getId());
                petWeb.setTag(pet.getTag());
                petWeb.setName(pet.getName());
                return petWeb;
            }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
