package com.doziem.jamTesSystem.response;

import com.doziem.jamTesSystem.constant.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String profileId;

    private String userId;

    private String name;

    private String email;

    private String phone;

    private String bio;

    private String profileImageUrl;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String address;

    private String city;

    private String state;

    private String country;
}
