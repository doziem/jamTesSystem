package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.request.ProfileRequest;
import com.doziem.jamTesSystem.response.ProfileResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<UserDto> getAllUsers();

    List<UserDto> getAllUsers(int page, int size);

    Optional<UserDto> getUserById(String id);

    Optional<UserDto> updateUser(String id, UserDto dto, String password, Authentication authentication);

    Optional<UserDto> deactivateUser(String id, Authentication authentication);

    Optional<String> deleteUser(String id, Authentication authentication);

    boolean canAccessUser(String id, Authentication authentication);

    boolean isAdmin(Authentication authentication);

    ProfileResponse createProfileResponse(String userId, ProfileRequest request) throws IOException;

    ProfileResponse getProfileResponse(String userId, Authentication authentication);
}
