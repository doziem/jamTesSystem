package com.doziem.jamTesSystem.service.medication;

import com.doziem.jamTesSystem.dto.MedicationDto;

import java.util.List;
import java.util.Optional;

public interface IMedicationService {

    List<MedicationDto> getAllMedications();

    List<MedicationDto> getAllMedications(int page, int size);

    Optional<MedicationDto> getMedicationById(String id);

    MedicationDto saveMedication(MedicationDto medicationDto, String supervisorId);

//    MedicationDto saveMedication(MedicationDto medicationDto);

    MedicationDto updateMedication(String id, MedicationDto medicationDto);

    void deleteMedication(String id);
}
