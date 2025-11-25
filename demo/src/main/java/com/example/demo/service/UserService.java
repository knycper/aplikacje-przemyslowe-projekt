package com.example.demo.service;

import com.example.demo.domain.dto.RegisterDTO;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exceptions.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        userRepository.findByUsername(req.getUsername()).ifPresent(u -> {throw new UserAlreadyExistsException("Given username already exists: " + u.getUsername());
        });

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }
}
