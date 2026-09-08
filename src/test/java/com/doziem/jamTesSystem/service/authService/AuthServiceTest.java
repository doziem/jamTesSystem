package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.mapper.UserMapper;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenForVerifiedUser() {
        AuthRequest request = new AuthRequest();
        request.setEmailOrPhone("jane@example.com");
        request.setPassword("Secret123");

        User user = User.builder()
                .id("u-1")
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("08012345678")
                .role(Role.PATIENT)
                .active(true)
                .verified(true)
                .password("encoded-password")
                .build();

        Authentication authentication = mock(Authentication.class);
        UserDetails principal = org.springframework.security.core.userdetails.User.withUsername("jane@example.com")
                .password("encoded-password")
                .roles("PATIENT")
                .build();

        when(userRepository.findByEmailOrPhone("jane@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtUtil.generateToken(principal)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("Login successful", response.getMessage());
        assertEquals("jwt-token", response.getToken());
        assertEquals("jane@example.com", response.getEmail());
    }

    @Test
    void createUserSavesVerifiedUserAndSendsEmail() {
        UserDto dto = UserDto.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("08012345678")
                .password("Secret123")
                .role(Role.PATIENT)
                .active(true)
                .build();

        User mappedUser = User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("08012345678")
                .role(Role.PATIENT)
                .active(true)
                .password("encoded-password")
                .build();

        User savedUser = User.builder()
                .id("u-1")
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("08012345678")
                .role(Role.PATIENT)
                .active(true)
                .verified(false)
                .emailVerificationToken("token-123")
                .password("encoded-password")
                .build();

        when(userRepository.findByEmailIgnoreCase("jane@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(dto, dto.getPassword(), passwordEncoder)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        AuthResponse response = authService.createUser(dto);

        assertNotNull(response);
        assertEquals("User created successfully. Please check your email for verification.", response.getMessage());
        assertEquals("jane@example.com", response.getEmail());
    }
}
