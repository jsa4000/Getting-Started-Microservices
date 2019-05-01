package com.example.petstore.service;

import com.example.petstore.web.api.PetsApiDelegate;
import com.example.petstore.web.api.model.Pet;
import com.example.petstore.web.api.model.Pets;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Service
public class PetstoreService implements PetsApiDelegate{

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Void> createPets(Pet pet) {
        return null;
    }

    @Override
    public ResponseEntity<Pets> listPets(Integer limit) {

        Pets result = new Pets();

        Pet p1 = new Pet();
        p1.setId(1L);
        p1.setName("Pet1");
        p1.setTag("dog");

        Pet p2 = new Pet();
        p2.setId(2L);
        p2.setName("Pet2");
        p2.setTag("cat");

        result.add(p1);
        result.add(p2);

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Pets> showPetById(String petId) {
        return null;
    }
}
