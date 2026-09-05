package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    UserDto createUser(UserDto dto);

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(String id);

    Optional<UserDto> updateUser(String id, UserDto dto, String password);

    Optional<String> deleteUser(String id);
}
