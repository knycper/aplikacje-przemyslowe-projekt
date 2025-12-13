package com.example.demo.security.model;

import com.example.demo.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public UUID getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // hasło z bazy
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // login z bazy
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // no roles
    }
}
