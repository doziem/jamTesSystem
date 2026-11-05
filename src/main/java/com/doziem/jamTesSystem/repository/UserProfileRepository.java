package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    boolean existsByUserId(String userId);

    Optional<UserProfile> findByUserId(String userId);
}
