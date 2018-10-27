package com.example.oauthservice.security;

import com.example.oauthservice.model.Role;
import com.example.oauthservice.model.User;
import com.example.oauthservice.service.RoleService;
import com.example.oauthservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new UserDetailsEnhanced(user, getAuthorities(user));
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        Map<String,Role> rolesMap = new HashMap<String,Role>();
        roleService.findAll().stream().forEach(x-> rolesMap.put(x.getId(), x));
        return user.getRoles().stream()
                .map(x -> new SimpleGrantedAuthority("ROLE_" + rolesMap.get(x).getName())).collect(Collectors.toList());
    }
}
