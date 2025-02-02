package com.doziem.jamTesSystem.response;

import com.doziem.jamTesSystem.constant.Role;

public class AuthResponse {
    private final String message;
    private  String name;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private  String token;


    public AuthResponse(String message){
        this.message= message;
    }
    public AuthResponse(String message,String name,String email, String phone,Role role,boolean active, String token) {
        this.message = message;
        this.name = name;
        this.email=email;
        this.phone =phone;
        this.role = role;
        this.active = active;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

