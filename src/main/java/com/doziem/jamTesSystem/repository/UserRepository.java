package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
