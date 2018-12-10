package com.example.management.service;

import com.example.management.model.User;
import com.example.management.repository.UserRepository;
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

    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(String id) { return userRepository.findById(id); }

    public Optional<User> findByName(String name) { return userRepository.findByUsername(name); }

    public User save(User user){
        Optional<User> exitingUser = userRepository.findByUsername(user.getUsername());
        if (exitingUser.isPresent()){
            user.setId(exitingUser.get().getId());
        }
        return userRepository.save(user);
    }

    public void delete(String id){ userRepository.deleteById(id); }
}
