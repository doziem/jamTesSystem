package com.doziem.jamTesSystem.repository;

import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.PharmacyInventory;
import com.doziem.jamTesSystem.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyInventoryRepository extends JpaRepository<PharmacyInventory, String> {
    List<PharmacyInventory> findByPharmacyId(String pharmacyId);
    Optional<PharmacyInventory> findByPharmacyAndMedication(Pharmacy pharmacy, Medication medication);
    Optional<PharmacyInventory> findByPharmacyIdAndMedicationId(String pharmacyId, String medicationId);
}
