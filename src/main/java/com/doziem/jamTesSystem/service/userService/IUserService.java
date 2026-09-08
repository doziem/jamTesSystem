package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<UserDto> getAllUsers();

    List<UserDto> getAllUsers(int page, int size);

    Optional<UserDto> getUserById(String id);

    Optional<UserDto> updateUser(String id, UserDto dto, String password);

    Optional<UserDto> deactivateUser(String id);

    Optional<String> deleteUser(String id);
}
