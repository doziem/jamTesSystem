package com.doziem.jamTesSystem.service.authService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;

public interface IAuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse createUser(UserDto dto);

}
