package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatienceService implements IPatientService{
    private final PatientRepository patientRepository;

    public PatienceService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientDto createPatient(PatientDto patientDTO) {
        Patient patient = PatientDto.mapToEntity(patientDTO);
        patient = patientRepository.save(patient);
        return PatientDto.mapToDTO(patient);
    }

    @Override
    public PatientDto getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return PatientDto.mapToDTO(patient);
    }

    @Override
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientDto::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDto updatePatient(UUID id, PatientDto patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Patient updatedPatient = patientRepository.save(updateExistingPatient(existingPatient,patientDTO));
        return PatientDto.mapToDTO(updatedPatient);

    }

    private Patient updateExistingPatient(Patient existingPatient,PatientDto patientDTO) {
        existingPatient.setFirstName(patientDTO.getFirstName());
        existingPatient.setLastName(patientDTO.getLastName());
        existingPatient.setEmail(patientDTO.getEmail());
        existingPatient.setPhone(patientDTO.getPhone());
        existingPatient.setDateOfBirth(patientDTO.getDateOfBirth());
        existingPatient.setGender(patientDTO.getGender());
        existingPatient.setAddress(patientDTO.getAddress());
        existingPatient.setActive(patientDTO.isActive());
        return existingPatient;
    }

    @Override
    public void deletePatient(UUID id) {
      Patient patient=  patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);

    }
}
