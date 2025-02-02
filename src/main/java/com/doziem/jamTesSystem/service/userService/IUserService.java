package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    UserDto createUser(UserDto dto);

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(UUID id);

    Optional<UserDto> updateUser(UUID id, UserDto dto, String password);

    Optional<String> deleteUser(UUID id);
}
