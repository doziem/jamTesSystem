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

}
