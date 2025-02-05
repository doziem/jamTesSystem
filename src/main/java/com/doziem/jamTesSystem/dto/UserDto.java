package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.model.Address;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;



public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String password;
    @JsonProperty("phone")
    private String phone;
    private Role role;
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Convert User Entity to UserDto
    public static UserDto mapToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return dto;
    }

        // Convert UserDto to User Entity
    public static User mapToEntity(UserDto dto, String password, BCryptPasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(user.getPassword());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        user.setActive(dto.isActive());
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

}
