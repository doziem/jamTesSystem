package com.doziem.jamTesSystem.model;

import com.doziem.jamTesSystem.constant.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private String userProfileId;

    @OneToOne
    @JoinColumn(name = "id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profileImageUrl;

    private String profileImagePublicId;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String city;

    private String state;

    private String country;

    @PrePersist
    public void generateId() {
        if (this.userProfileId == null || this.userProfileId.isBlank()) {
            this.userProfileId = UUID.randomUUID().toString();
        }
    }
}
