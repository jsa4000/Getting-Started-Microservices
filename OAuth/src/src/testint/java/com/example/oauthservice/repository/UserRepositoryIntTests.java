package com.example.oauthservice.repository;

import com.example.oauthservice.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class UserRepositoryIntTests {

    @Autowired
    UserRepository repository;

    @Before
    public void init() {
        deleteUsersByName(getDefaultUser());
    }

    @Test
    public void insert_CreateNormalUser_ShouldReturnOk() {
        User expectedUser = getDefaultUser();

        repository.insert(expectedUser);

        Optional<User> newUser = repository.findById(expectedUser.getId());

        assertTrue("User returned must be found",newUser.isPresent());
        assertTrue("User returned name must be equal to expected,",
                expectedUser.getUsername().equals(newUser.get().getUsername()));
        assertTrue("User returned password must be equal to expected,",
                expectedUser.getPassword().equals(newUser.get().getPassword()));

        repository.deleteById(newUser.get().getId());
    }

    @Test
    public void findByUserName_UserCreatedWithName_ShouldReturnOk() {
        User expectedUser = getDefaultUser();

        repository.insert(expectedUser);

        List<User> usersFound = repository.findByUsername(expectedUser.getUsername());

        assertTrue("Users found must be greater than zero",usersFound.size() > 0);
        assertTrue("Users found must be greater than zero and username equal to expected",
                usersFound.stream()
                        .allMatch(x -> x.getUsername().equals(expectedUser.getUsername())));

        usersFound.stream().forEach(x -> repository.deleteById(x.getId()));
    }

    @Test
    public void findByUserName_UserNotCreated_ShouldReturnOk() {
        User userNotFound = new User();
        userNotFound.setUsername("testUserUnknown");

        List<User> usersFound = repository.findByUsername(userNotFound.getUsername());

        assertTrue("Users found must be zero",usersFound.size() == 0);
    }

    private User getDefaultUser()
    {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        return user;
    }

    private void deleteUsersByName(User user) {
        repository.deleteAll(repository.findByUsername(user.getUsername()));
    }

}
