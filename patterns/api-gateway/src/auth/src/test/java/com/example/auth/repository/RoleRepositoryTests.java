package com.example.auth.repository;

import com.example.auth.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleRepositoryTests {

    @Autowired
    RoleRepository repository;

    @Test
    public void createRole_valid_shouldReturnOk() {
        Role expectedRole = getDefaultRole();

        Role actualRole = repository.save(expectedRole);

        assertTrue(actualRole.getName().equals(expectedRole.getName()));
        assertTrue(actualRole.getId().equals(expectedRole.getId()));

        repository.deleteById(actualRole.getId());
    }

    @Test
    public void findByName_validRoleName_shouldReturnOk() {
        Role expectedRole = getDefaultRole();
        expectedRole = repository.save(expectedRole);

        Optional<Role> actualRole = repository.findByName(expectedRole.getName());
        assertTrue(actualRole.isPresent());

        repository.deleteById(expectedRole.getId());
    }

    private Role getDefaultRole() {
        return new Role("ROLE_TEST_ID","ROLE_TEST_NAME");
    }
}
