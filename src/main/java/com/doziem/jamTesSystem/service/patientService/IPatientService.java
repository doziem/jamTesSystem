package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;

import java.util.List;

public interface IPatientService {
    PatientDto createPatient(PatientDto patientDTO);
    PatientDto getPatientById(String id);
    List<PatientDto> getAllPatients();
    PatientDto updatePatient(String id, PatientDto patientDTO);
    void deletePatient(String id);
}
