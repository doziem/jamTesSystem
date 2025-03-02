package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.model.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPrescriptionService {
    List<PrescriptionDto> getAllPrescriptions();

    Optional<PrescriptionDto> getPrescriptionById(UUID id);

    PrescriptionDto savePrescription(PrescriptionDto prescriptionDto);

    PrescriptionDto updatePrescription(UUID id, PrescriptionDto prescriptionDto);

    void deletePrescription(UUID id);
}
