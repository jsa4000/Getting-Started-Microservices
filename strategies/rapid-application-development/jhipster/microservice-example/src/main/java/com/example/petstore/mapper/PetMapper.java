package com.example.petstore.mapper;

import com.example.petstore.domain.Pet;
import com.example.petstore.web.api.model.NewPetDto;
import com.example.petstore.web.api.model.PetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    Pet toDomain(NewPetDto pet);

    PetDto fromDomain(Pet pet);

}
