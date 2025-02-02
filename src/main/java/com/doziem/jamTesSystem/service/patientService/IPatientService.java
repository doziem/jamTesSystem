package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;

import java.util.List;
import java.util.UUID;

public interface IPatientService {
    PatientDto createPatient(PatientDto patientDTO);
    PatientDto getPatientById(UUID id);
    List<PatientDto> getAllPatients();
    PatientDto updatePatient(UUID id, PatientDto patientDTO);
    void deletePatient(UUID id);
}
