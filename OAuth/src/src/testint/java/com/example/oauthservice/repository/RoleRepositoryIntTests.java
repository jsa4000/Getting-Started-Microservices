package com.example.oauthservice.repository;

import com.example.oauthservice.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class RoleRepositoryIntTests {

    @Autowired
    RoleRepository repository;

    @Before
    public void init() {
       deleteRoleByName(getDefaultRole());
    }

    @Test
    public void createRole_CreatedValidRole_ShouldReturnOk() {
        Role expectedRole = getDefaultRole();

        repository.insert(expectedRole);

        Optional<Role> newRole = repository.findById(expectedRole.getId());

        repository.deleteById(newRole.get().getId());

        assertTrue("Role returned must be found",newRole.isPresent());
        assertTrue("Role returned name must be equal to expected,",
                expectedRole.getName().equals(newRole.get().getName()));
    }

    @Test
    public void createRole_CreatedRoleWithSameName_ShouldReturnError() {
        Role firstSameRole = repository.insert(getDefaultRole());
        assertTrue("First Role cannot be null",firstSameRole != null);

        try{
            Role secondSameRole = repository.insert(getDefaultRole());
            assertTrue("Seconds Role cannot have the same Rolename",secondSameRole != null);
        } catch(Exception e) { }
        finally {
            repository.deleteById(firstSameRole.getId());
        }
    }

    @Test
    public void findByName_CreatedValidUser_ShouldReturnOk() {
        Role expectedRole = getDefaultRole();

        repository.insert(expectedRole);

        Role roleFound = repository.findByName(expectedRole.getName());

        repository.deleteById(expectedRole.getId());

        assertTrue("Role found cannot be null",roleFound != null);
        assertTrue("Role found name must be equal to expected",
                roleFound.getName().equals(expectedRole.getName()));
    }

    @Test
    public void findByName_RoleNotCreated_ShouldReturnNotFound() {
        Role roleNotFound = new Role(null, "UNKNOWN");

        Role roleFound = repository.findByName(roleNotFound.getName());

        assertTrue("Role found must be null",roleFound == null);
    }

    private Role getDefaultRole()
    {
        return new Role(null,"ADMIN");
     }

    private void deleteRoleByName(Role Role) {
        Role RoleFound = repository.findByName(Role.getName());
        if (RoleFound != null)
            repository.delete(RoleFound);
    }

}
