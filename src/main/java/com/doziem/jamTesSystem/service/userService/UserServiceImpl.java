package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto dto) {
        if (dto == null || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        User user = UserDto.mapToEntity(dto, dto.getPassword(), passwordEncoder);
        return UserDto.mapToDTO(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::mapToDTO)
                .toList();
    }

    @Override
    public Optional<UserDto> getUserById(String id) {
        return userRepository.findById(id).map(UserDto::mapToDTO);
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
            return UserDto.mapToDTO(userRepository.save(user));
        });
    }

    @Override
    public Optional<String> deleteUser(String id) {
        return userRepository.findById(id).map(user -> {
            userRepository.deleteById(id);
            return "User deleted successfully.";
        });
    }
}
