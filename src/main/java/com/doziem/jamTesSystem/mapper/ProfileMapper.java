package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.model.UserProfile;
import com.doziem.jamTesSystem.request.ProfileRequest;
import com.doziem.jamTesSystem.response.ProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public static UserProfile toEntity(ProfileRequest request, User user) {
        return UserProfile.builder()
                .user(user)
                .bio(request.getBio())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .build();
    }

    public static ProfileResponse toResponse(UserProfile profile){
        User user = profile.getUser();

        return ProfileResponse.builder()
                .profileId(profile.getUserProfileId())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .address(profile.getAddress())
                .city(profile.getCity())
                .state(profile.getState())
                .country(profile.getCountry())
                .build();
    }
}
