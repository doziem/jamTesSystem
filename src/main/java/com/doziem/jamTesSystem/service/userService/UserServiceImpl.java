package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.mapper.UserMapper;
import com.doziem.jamTesSystem.repository.UserRepository;
import com.doziem.jamTesSystem.service.emailService.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return getAllUsers(0, 10);
    }

    @Override
    public List<UserDto> getAllUsers(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        return userRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public Optional<UserDto> getUserById(String id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> updateUser(String id, UserDto dto, String password) {
        return userRepository.findById(id).map(user -> {
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getRole() != null) user.setRole(dto.getRole());
            if (dto.getPhone() != null) user.setPhone(dto.getPhone());
            if (dto.isActive() != user.isActive()) user.setActive(dto.isActive());
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            return userMapper.toDto(userRepository.save(user));
        });
    }

    @Override
    public Optional<UserDto> deactivateUser(String id) {
        return userRepository.findById(id).map(user -> {
            user.setActive(false);
            return userMapper.toDto(userRepository.save(user));
        });
    }

    @Override
    public Optional<String> deleteUser(String id) {
        return userRepository.findById(id).map(user -> {
            if (user.isActive()) {
                throw new IllegalStateException("Only deactivated accounts can be deleted");
            }
            userRepository.deleteById(id);
            return "User deleted successfully.";
        });
    }
}
