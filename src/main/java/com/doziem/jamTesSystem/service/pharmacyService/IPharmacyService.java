package com.doziem.jamTesSystem.service.pharmacyService;

import com.doziem.jamTesSystem.dto.PharmacyDto;
import com.doziem.jamTesSystem.dto.PharmacyInventoryDto;

import java.util.List;
import java.util.Optional;

public interface IPharmacyService {
    PharmacyDto createMainPharmacy(PharmacyDto pharmacyDto);
    PharmacyDto createDepartmentPharmacy(String mainPharmacyId, PharmacyDto pharmacyDto);
    List<PharmacyDto> getAllPharmacies();
    Optional<PharmacyDto> getPharmacyById(String id);
    PharmacyInventoryDto addMedicineToPharmacy(String pharmacyId, String medicationId, int quantity);
    PharmacyInventoryDto transferMedicationFromMain(String departmentPharmacyId, String medicationId, int quantity);
    String confirmPaymentAndDispense(String prescriptionId, String pharmacyId);
}
