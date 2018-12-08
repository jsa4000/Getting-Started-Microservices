package com.example.management.repository;

import com.example.management.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    UserRepository repository;

    @Test
    public void createUser_valid_shouldReturnOk() {
        User expectedUser = getDefaultUser();

        User actualUser = repository.save(expectedUser);

        assertTrue(actualUser.getEmail().equals(expectedUser.getEmail()));
        assertTrue(actualUser.getPassword().equals(expectedUser.getPassword()));
        assertTrue(actualUser.getUsername().equals(expectedUser.getUsername()));

        repository.deleteById(actualUser.getId());
    }

    @Test
    public void findByEmail_validUserName_shouldReturnOk() {
        User expectedUser = getDefaultUser();
        expectedUser = repository.save(expectedUser);

        List<User> actualUser = repository.findByEmail(expectedUser.getEmail());
        assertTrue(actualUser.stream().findAny().isPresent());

        repository.deleteById(expectedUser.getId());
    }

    private User getDefaultUser() {
        return new User(null,"Javier","Santos","jsa4000@gmail.com", true,
                null,null);
    }
}
