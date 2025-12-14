package com.example.demo.service;

import com.example.demo.domain.dto.RegisterDTO;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exceptions.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.model.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterDTO req) {
        userRepository.findByUsername(req.getUsername()).ifPresent(u -> {throw new UserAlreadyExistsException("User with this username already exists: " + u.getUsername());
        });

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }

    public UUID getLoggedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}
