package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
