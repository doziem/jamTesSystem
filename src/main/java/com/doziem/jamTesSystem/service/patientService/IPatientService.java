package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;

import java.util.List;

public interface IPatientService {
    PatientDto createPatient(PatientDto patientDTO);
    PatientDto getPatientById(String id);
//    add pagination to getAllPatients method
    List<PatientDto> getAllPatients(int page, int size);
    PatientDto updatePatient(String id, PatientDto patientDTO);
    void deletePatient(String id);
}
