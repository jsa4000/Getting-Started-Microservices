package com.example.petstore.service;

import com.example.petstore.web.api.PetsApiDelegate;
import com.example.petstore.web.api.model.NewPet;
import com.example.petstore.web.api.model.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PetstoreService implements PetsApiDelegate{

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Pet> addPet(NewPet newPet) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deletePet(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<Pet> findPetById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Pet>> findPets(List<String> tags, Integer limit) {
        Pet p1 = new Pet();
        p1.setId(1L);
        p1.setName("Pet1");
        p1.setTag("dog");

        Pet p2 = new Pet();
        p2.setId(2L);
        p2.setName("Pet2");
        p2.setTag("cat");

        return ResponseEntity.ok(Arrays.asList(p1,p2));
    }

}
