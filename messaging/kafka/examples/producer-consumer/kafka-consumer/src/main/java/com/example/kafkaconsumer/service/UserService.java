package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.User;
import com.example.kafkaconsumer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable("users")
    public List<User> findAll() {
        log.info("Refreshing findAll cache");
        return userRepository.findAll();
    }

    @Cacheable("users")
    public User findById(String id) {
        log.info("Refreshing findById {} cache", id);
        return userRepository.findById(id).get();
    }

    @Cacheable("users")
    public User findByUsername(String name) {
        log.info("Refreshing findByUsername {} cache", name);
        return userRepository.findByUsername(name);
    }

    public User save(User user){
        User exitingUser = userRepository.findByUsername(user.getUsername());
        if (exitingUser != null){
            user.setId(exitingUser.getId());
        }
        return userRepository.save(user);
    }

    public void delete(String id){ userRepository.deleteById(id); }
}
