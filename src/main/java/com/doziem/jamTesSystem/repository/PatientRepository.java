package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByMrn(String mrn);
    Optional<Patient> findByPhone(String phone);
    boolean existsByMrn(String mrn);
}
