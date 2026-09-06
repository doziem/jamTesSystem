package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.model.Patient;

import java.util.List;
import java.util.Optional;

public interface IPrescriptionService {
    List<PrescriptionDto> getAllPrescriptions();

    List<PrescriptionDto> getAllPrescriptions(int page, int size);

    Optional<PrescriptionDto> getPrescriptionById(String id);

    PrescriptionDto savePrescription(PrescriptionDto prescriptionDto);

    PrescriptionDto updatePrescription(String id, PrescriptionDto prescriptionDto);

    void deletePrescription(String id);
}
