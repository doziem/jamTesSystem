package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.UserDto;
import com.doziem.jamTesSystem.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .verified(user.isVerified())
                .emailVerificationToken(user.getEmailVerificationToken())
                .build();
    }

    public User toEntity(UserDto dto, String password, BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .active(dto.isActive())
                .verified(dto.isVerified())
                .password(passwordEncoder.encode(password))
                .build();
    }
}
