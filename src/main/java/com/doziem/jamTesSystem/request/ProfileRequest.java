package com.doziem.jamTesSystem.request;

import com.doziem.jamTesSystem.constant.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String bio;

    private String profileImageUrl;

    private String profileImagePublicId;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String address;

    private String city;

    private String state;

    private String country;
}
