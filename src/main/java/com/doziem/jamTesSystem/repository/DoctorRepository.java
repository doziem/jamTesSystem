package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByUser_Id(String userId);
}
