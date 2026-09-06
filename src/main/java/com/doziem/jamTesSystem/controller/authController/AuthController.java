package com.doziem.jamTesSystem.controller.authController;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.userService.IUserService;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.service.authService.AuthService;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final IUserService userService;

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IUserService userService, UserRepository userRepository, IUserService userService1, AuthService authService, EmailService emailService) {
        this.userService = userService1;
        this.authService = authService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage() != null ? e.getMessage() : "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, "User created successfully. Please check your email for verification.", createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage() != null ? e.getMessage() : "Error creating user"));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String email, @RequestParam(required = false) String token) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        User user = userOptional.get();
        if (token != null && !token.isBlank()) {
            if (user.getEmailVerificationToken() == null || !user.getEmailVerificationToken().equals(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid verification token"));
            }
        }

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully", user.getEmail()));
    }
}