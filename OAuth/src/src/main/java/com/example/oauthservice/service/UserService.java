package com.example.oauthservice.service;

import com.example.oauthservice.model.User;
import com.example.oauthservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service(value = "userService")
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User save(User user){
        return save(user);
    }

    public void delete(String id){
        repository.deleteById(id);
    }

    private List getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> user = repository.findByUsername(username);
        if(user.size() == 0){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.get(0).getUsername(),
                                                                      user.get(0).getPassword(), getAuthority());
    }
}
