package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PatientRepository extends JpaRepository<Patient, String> {
}
