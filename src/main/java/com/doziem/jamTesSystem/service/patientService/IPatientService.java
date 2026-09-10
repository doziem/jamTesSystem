package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;

import java.util.List;

import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.request.EncounterStatusRequest;
import com.doziem.jamTesSystem.request.PatientArrivalRequest;
import com.doziem.jamTesSystem.response.PatientArrivalResponse;

public interface IPatientService {
    PatientDto createPatient(PatientDto patientDTO);
    PatientDto getPatientById(String id);
    PatientDto getPatientByMrn(String mrn);
    List<PatientDto> searchPatients(String mrn, String phone, String name, String dob);
    PatientArrivalResponse arrivePatient(PatientArrivalRequest request);
    List<DoctorDto> getAssignableDoctors();
    List<EncounterDto> getPatientVisitHistory(String patientId);
    EncounterDto updateEncounterStatus(String encounterId, EncounterStatusRequest request);
    List<PatientDto> getAllPatients(int page, int size);
    PatientDto updatePatient(String id, PatientDto patientDTO);
    void deletePatient(String id);
}
