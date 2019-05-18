package com.example.ecommerce.pet.mapper;

import com.example.ecommerce.pet.domain.Pet;
import com.example.ecommerce.pet.web.api.model.NewPetDto;
import com.example.ecommerce.pet.web.api.model.PetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    Pet toDomain(NewPetDto pet);

    PetDto fromDomain(Pet pet);

}
