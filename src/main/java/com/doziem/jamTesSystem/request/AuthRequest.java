package com.doziem.jamTesSystem.request;


import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthRequest {
    private String password;

    @JsonProperty("emailOrPhone")
    private String emailOrPhone;

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone){
        this.emailOrPhone =emailOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
