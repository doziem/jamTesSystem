package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String name;
    private String email;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String password;

        @JsonProperty("phone")
        private String phone;

        private Role role;
        private boolean active;
        private boolean verified;
        private String emailVerificationToken;

        public static UserDto mapToDTO(User user) {
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

        public static User mapToEntity(UserDto dto, String password, BCryptPasswordEncoder passwordEncoder) {
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setRole(dto.getRole());
            user.setActive(dto.isActive());
            user.setVerified(dto.isVerified());
            user.setPassword(passwordEncoder.encode(password));
            return user;
        }
}
