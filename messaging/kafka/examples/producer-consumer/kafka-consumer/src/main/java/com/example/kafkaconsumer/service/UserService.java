package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.model.User;
import com.example.kafkaconsumer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).get();
    }

    public User save(User user){
        Optional<User> exitingUser = userRepository.findById(user.getId());
        if (exitingUser.isPresent()){
            user.setId(exitingUser.get().getId());
        }
        return userRepository.save(user);
    }

    public void delete(String id){ userRepository.deleteById(id); }
}
