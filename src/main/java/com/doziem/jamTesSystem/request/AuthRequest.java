package com.doziem.jamTesSystem.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AuthRequest {
    private String password;

    @JsonProperty("emailOrPhone")
    private String emailOrPhone;

}
