package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.mapper.UserMapper;
import com.doziem.jamTesSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<UserDto> updateUser(String id, UserDto dto, String password, Authentication authentication) {
        if (!canAccessUser(id, authentication)) {
            throw new UserNotAllowedException("You are not allowed to update this user");
        }

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
    public Optional<UserDto> deactivateUser(String id, Authentication authentication) {
        if (!canAccessUser(id, authentication)) {
            throw new UserNotAllowedException("You are not allowed to deactivate this user");
        }

        return userRepository.findById(id).map(user -> {
            user.setActive(false);
            return userMapper.toDto(userRepository.save(user));
        });
    }

    @Override
    public Optional<String> deleteUser(String id, Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new UserNotAllowedException("Only admin users can delete users");
        }

        return userRepository.findById(id).map(user -> {
            if (user.isActive()) {
                throw new IllegalStateException("Only deactivated accounts can be deleted");
            }
            userRepository.deleteById(id);
            return "User deleted successfully.";
        });
    }

    @Override
    public boolean canAccessUser(String id, Authentication authentication) {
        return isAdmin(authentication) || isCurrentUser(id, authentication);
    }

    @Override
    public boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private boolean isCurrentUser(String id, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return false;
        }

        return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .map(user -> user.getId().equals(id))
                .orElse(false);
    }
}
