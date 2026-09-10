package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.response.AuthResponse;
import org.springframework.security.core.Authentication;

public interface IAuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse createUser(UserDto dto);

    ApiResponse verifyEmail(String email, String token);

    UserDto getCurrentUser(Authentication authentication);

}
