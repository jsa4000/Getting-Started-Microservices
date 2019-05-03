package com.example.micro.mapper;

import com.example.micro.domain.Pet;
import com.example.micro.web.api.model.NewPetDto;
import com.example.micro.web.api.model.PetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    Pet toDomain(NewPetDto pet);

    PetDto fromDomain(Pet pet);

}
