package com.example.oauthservice.service;

import com.example.oauthservice.model.Role;
import com.example.oauthservice.model.User;
import com.example.oauthservice.repository.RoleRepository;
import com.example.oauthservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(value = "userService")
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(String id) { return userRepository.findById(id).get(); }

    public User save(User user){
        User exitingUser = userRepository.findByUsername(user.getUsername());
        if (exitingUser != null){
            user.setId(exitingUser.getId());
        }
        return userRepository.save(user);
    }

    public void delete(String id){ userRepository.deleteById(id); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                                user.getPassword(), getAuthority(user));
    }

    private List<GrantedAuthority> getAuthority(User user) {
        Map<String,Role> rolesMap = new HashMap<String,Role>();
        roleRepository.findAll().stream().forEach(x-> rolesMap.put(x.getId(), x));
        return user.getRoles().stream()
                .map(x -> new SimpleGrantedAuthority("ROLE_" + rolesMap.get(x).getName())).collect(Collectors.toList());
    }
}
