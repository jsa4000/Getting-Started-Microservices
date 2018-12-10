package com.example.gateway.repository.client;

import com.example.gateway.model.User;
import com.example.gateway.repository.impl.UserManagementClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserManagementClientIntTests {

    @Autowired
    private UserManagementClient client;

    @ClassRule
    public static DockerComposeContainer compose =
            new DockerComposeContainer(new File("../docker/docker-compose.yml"));

    @Test
    public void getClient_userExistingUser_shouldReturnOk() {
        String userName = "5c0e34c3c870cf3e74dcf741";

        String address = "http://" + compose.getServiceHost("mongodb_1", 80) + ":" + compose.getServicePort("mongodb_1", 80);

        Optional<User> user = client.getUserById(userName);

        assertTrue(user.isPresent());
    }

}
