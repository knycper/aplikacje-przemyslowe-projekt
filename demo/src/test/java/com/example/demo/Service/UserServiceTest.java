package com.example.demo.Service;

import com.example.demo.domain.dto.RegisterDTO;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.exceptions.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.model.CustomUserDetails;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ===== register() =====

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("john");
        dto.setPassword("secret");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        userService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("secret");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("john");
        dto.setPassword("secret");

        User existingUser = new User();
        existingUser.setUsername("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(dto));

        verify(userRepository, never()).save(any());
    }

    // ===== getLoggedUserId() =====

    @Test
    void shouldReturnLoggedUserId() {
        UUID userId = UUID.randomUUID();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        UUID result = userService.getLoggedUserId();

        assertEquals(userId, result);
    }
}
