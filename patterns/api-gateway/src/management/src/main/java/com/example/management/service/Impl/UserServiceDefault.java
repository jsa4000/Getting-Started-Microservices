package com.example.management.service.Impl;

import com.example.management.model.User;
import com.example.management.repository.UserRepository;
import com.example.management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceDefault implements UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(String id) { return userRepository.findById(id); }

    public User save(User user){ return userRepository.save(user); }

    public void delete(String id){ userRepository.deleteById(id); }
}
