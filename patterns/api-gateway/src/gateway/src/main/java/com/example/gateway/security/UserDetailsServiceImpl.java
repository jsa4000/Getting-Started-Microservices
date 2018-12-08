package com.example.gateway.security;

import com.example.gateway.client.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User(null, "admin", "$2a$10$S72w1YZ79LmH6xByZrmS0uhwoJJMlDr8cos4c2ubiByqHTM2jb5BG",
                "admin@google.com", true, null, null);
        return new UserDetailsEnhanced(user, getAuthorities(user));
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        List<String> roles = Arrays.asList("USER","ADMIN");
        return roles.stream()
                .map(x -> new SimpleGrantedAuthority("ROLE_" + x))
                .collect(Collectors.toList());
    }
}
