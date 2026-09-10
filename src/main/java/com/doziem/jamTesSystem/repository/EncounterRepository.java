package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, String> {
    List<Encounter> findByPatientIdOrderByArrivalTimeDesc(String patientId);
}
