package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.config.EmailUtil;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.mapper.UserMapper;
import com.doziem.jamTesSystem.model.Staff;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.StaffRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailUtil emailUtil;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final StaffRepository staffRepository;



    public AuthResponse login(AuthRequest request) {
        String loginIdentifier = request.getEmailOrPhone();

        Optional<User> optionalUser = userRepository.findByEmailOrPhone(loginIdentifier);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = optionalUser.get();
//        if (!user.isVerified()) {
//            if (user.getEmailVerificationToken() == null || user.getEmailVerificationToken().isBlank()) {
//                user.setEmailVerificationToken(UUID.randomUUID().toString());
//                userRepository.save(user);
//            }
//            emailService.sendVerificationReminder(user.getEmail(), user.getName(), user.getEmailVerificationToken());
//            throw new IllegalArgumentException("Email not verified. A verification link has been sent to your email. Please verify and log in again.");
//        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword())
        );

        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        Role effectiveRole = resolveRole(user.getId());

        return new AuthResponse("Login successful",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                effectiveRole,
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

        try {

//            emailUtil.sendVerificationMail(savedUser.getEmail(), "Email Verification", "Please verify your email using this token: " + savedUser.getEmailVerificationToken());
//            emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getEmailVerificationToken());
            return new AuthResponse(
                    "User created successfully. Please check your email for verification.",
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    resolveRole(savedUser.getId()),
                    savedUser.isActive(),
                    null
            );
        }catch (Exception e) {
            log.error("Failed to send verification email to {}", savedUser.getEmail(), e);
            throw new RuntimeException("User created, but failed to send verification email. Please contact support.");
        }

    }

    @Override
    public ApiResponse verifyEmail(String email, String token) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Verification token is required");
        }

        if (user.getEmailVerificationToken() == null || !user.getEmailVerificationToken().equals(token)) {
            throw new IllegalArgumentException("Invalid verification token");
        }

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        return new ApiResponse(true, "Email verified successfully", user.getEmail());
    }

    @Override
    public UserDto getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Unauthorized");
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toDto(user);
    }

    private Role resolveRole(String userId) {
        return staffRepository.findByUser_Id(userId)
                .map(Staff::getRole)
                .orElse(Role.STAFF);
    }
}
