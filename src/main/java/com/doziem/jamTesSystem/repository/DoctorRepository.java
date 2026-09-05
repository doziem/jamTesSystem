package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DoctorRepository extends JpaRepository<Doctor, String> {
}
