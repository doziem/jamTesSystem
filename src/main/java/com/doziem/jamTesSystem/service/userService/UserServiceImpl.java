package com.doziem.jamTesSystem.service.userService;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl  implements IUserService {

    @Autowired
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder,UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // Create User
    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserDto.mapToEntity(dto, dto.getPassword(), passwordEncoder);
        User savedUser = userRepository.save(user);
        return UserDto.mapToDTO(savedUser);
    }


    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {
        return userRepository.findById(id).map(UserDto::mapToDTO);
    }

    @Override
    public Optional<UserDto> updateUser(UUID id, UserDto dto, String password) {
        return userRepository.findById(id).map(user -> {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setRole(dto.getRole());  // Directly using Role enum
            user.setActive(dto.isActive());
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            User updatedUser = userRepository.save(user);
            return UserDto.mapToDTO(updatedUser);
        });
    }

    @Override
    public Optional<String> deleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.deleteById(id);
            return Optional.of("User deleted successfully.");
        }).orElse(Optional.empty());
    }

}
