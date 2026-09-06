package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.config.JwtUtil;
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
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Autowired
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
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
                user.getName(),user.getEmail(),user.getPhone(),
                user.getRole(),user.isActive(), token);
    }
}

