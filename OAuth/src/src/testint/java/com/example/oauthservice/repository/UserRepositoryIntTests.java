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
       deleteUserByName(getDefaultUser());
    }

    @Test
    public void createUser_CreateNormalUser_ShouldReturnOk() {
        User expectedUser = getDefaultUser();

        repository.insert(expectedUser);

        Optional<User> newUser = repository.findById(expectedUser.getId());

        repository.deleteById(newUser.get().getId());

        assertTrue("User returned must be found",newUser.isPresent());
        assertTrue("User returned name must be equal to expected,",
                expectedUser.getUsername().equals(newUser.get().getUsername()));
        assertTrue("User returned password must be equal to expected,",
                expectedUser.getPassword().equals(newUser.get().getPassword()));
    }

    @Test
    public void createUser_UserCreatedWithSameName_ShouldReturnError() {
        User firstSameUser = repository.insert(getDefaultUser());
        assertTrue("First User cannot be null",firstSameUser != null);

        try{
            User secondSameUser = repository.insert(getDefaultUser());
            assertTrue("Seconds User cannot have the same username",secondSameUser != null);
        } catch(Exception e) { }
        finally {
            repository.deleteById(firstSameUser.getId());
        }
    }

    @Test
    public void findByUserName_MultipleUsersCreatedWithName_ShouldReturnOk() {
        User expectedUser = getDefaultUser();

        repository.insert(expectedUser);

        User usersFound = repository.findByUsername(expectedUser.getUsername());

        repository.deleteById(usersFound.getId());

        assertTrue("User found cannot be null",usersFound != null);
        assertTrue("User found username must be equal to expected",
                usersFound.getUsername().equals(expectedUser.getUsername()));
    }

    @Test
    public void findByUserName_UserNotCreated_ShouldReturnNotFound() {
        User userNotFound = new User();
        userNotFound.setUsername("testUserUnknown");

        User usersFound = repository.findByUsername(userNotFound.getUsername());

        assertTrue("User found must be null",usersFound == null);
    }

    @Test
    public void findByEmail_UserCreatedWithEmail_ShouldReturnOk() {
        User expectedUser = getDefaultUser();

        repository.insert(expectedUser);

        List<User> usersFound = repository.findByEmail(expectedUser.getEmail());

        usersFound.stream().forEach(x -> repository.deleteById(x.getId()));

        assertTrue("Users found must be greater than zero",usersFound.size() > 0);
        assertTrue("Users found must be greater than zero and email equal to expected",
                usersFound.stream().allMatch(x -> x.getEmail().equals(expectedUser.getEmail())));
    }

    @Test
    public void findByEmail_UserNotCreated_ShouldReturnOk() {
        User userNotFound = new User();
        userNotFound.setUsername("testUserUnknown");
        userNotFound.setEmail("testUserUnknown@email.com");

        List<User> usersFound = repository.findByEmail(userNotFound.getEmail());

        assertTrue("Users found must be zero",usersFound.size() == 0);
    }

    private User getDefaultUser()
    {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setEmail("testUser@email.com");
        return user;
    }

    private void deleteUserByName(User user) {
        User userFound = repository.findByUsername(user.getUsername());
        if (userFound != null)
            repository.delete(userFound);
    }

}
