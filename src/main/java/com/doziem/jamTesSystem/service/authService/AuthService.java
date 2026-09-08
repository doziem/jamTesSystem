package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.mapper.UserMapper;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService implements IAuthService{

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    public AuthResponse login(AuthRequest request) {
        String loginIdentifier = request.getEmailOrPhone();

        Optional<User> optionalUser = userRepository.findByEmailOrPhone(loginIdentifier);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = optionalUser.get();
        if (!user.isVerified()) {
            emailService.sendVerificationReminder(user.getEmail(), user.getName());
            throw new RuntimeException("Please verify your email before logging in.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword())
        );

        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

        return new AuthResponse("Login successful",
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                token);
    }

    @Override
    public AuthResponse createUser(UserDto dto) {
        if (dto == null || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = userMapper.toEntity(dto, dto.getPassword(), passwordEncoder);
        user.setVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);

        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getEmailVerificationToken());
        return new AuthResponse(
                "User created successfully. Please check your email for verification.",
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.isActive(),
                null
        );
    }
}
