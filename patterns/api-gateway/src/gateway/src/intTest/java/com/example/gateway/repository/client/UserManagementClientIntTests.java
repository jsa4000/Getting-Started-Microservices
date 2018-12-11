package com.example.gateway.repository.client;

import com.example.gateway.repository.impl.UserRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserManagementClientIntTests {

    @Autowired
    private UserRestClient client;

//    @ClassRule
//    public static GenericContainer consul =
//            new GenericContainer("bitnami/consul:latest").withExposedPorts(8500);
//
//    @ClassRule
//    public static GenericContainer mongodb =
//            new GenericContainer("mongo:latest")
//                    .withExposedPorts(27017)
//                    .withEnv("MONGO_INITDB_ROOT_USERNAME","root")
//                    .withEnv("MONGO_INITDB_ROOT_PASSWORD","root");
//
//    @ClassRule
//    public static GenericContainer managment =
//            new GenericContainer("management:latest")
//                    .withExposedPorts(8083)
//                    .withEnv("SPRING_PROFILES_ACTIVE","local");
//
//    @Test
//    public void getClient_userExistingUser_shouldReturnOk() {
//        String userName = "root";
//
//        Optional<User> user = client.getUserById(userName);
//
//        assertTrue(user.isPresent());
//    }

    @Test
    public void getClient_userExistingUser_shouldReturnOk() {

    }

}
