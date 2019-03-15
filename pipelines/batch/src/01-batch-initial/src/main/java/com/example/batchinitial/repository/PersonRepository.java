package com.example.batchinitial.repository;

import com.example.batchinitial.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
