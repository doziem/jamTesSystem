package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {
    boolean existsByUser_Id(String userId);

    Optional<Staff> findByUser_Id(String userId);

    List<Staff> findByRole(Role role);
}
