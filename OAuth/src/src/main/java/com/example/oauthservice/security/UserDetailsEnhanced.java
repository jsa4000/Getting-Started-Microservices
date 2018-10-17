package com.example.oauthservice.security;

import com.example.oauthservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
public class UserDetailsEnhanced extends User implements UserDetails {

    private List<GrantedAuthority> authorities;

    public UserDetailsEnhanced(User user, List<GrantedAuthority> authorities) {
        super(user.getId(),user.getUsername(),user.getPassword(),user.getEmail(),user.getResources(),user.getRoles());
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
