package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IPrescriptionService {
    List<PrescriptionDto> getAllPrescriptions();

    List<PrescriptionDto> getAllPrescriptions(int page, int size);

    Optional<PrescriptionDto> getPrescriptionById(String id);

    PrescriptionDto savePrescription(PrescriptionDto prescriptionDto);

    PrescriptionDto prescribeMedication(PrescriptionDto prescriptionDto, Authentication authentication);

    List<PrescriptionDto> getPharmacyPrescriptions(String pharmacyId);

    PrescriptionDto estimatePrescription(String pharmacyId, String prescriptionId);

    PrescriptionDto updatePrescription(String id, PrescriptionDto prescriptionDto, Authentication authentication);

    void deletePrescription(String id);
}
