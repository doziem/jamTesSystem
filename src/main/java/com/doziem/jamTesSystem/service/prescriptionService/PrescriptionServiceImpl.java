package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PrescriptionServiceImpl implements IPrescriptionService{

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public List<PrescriptionDto> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionDto::mapToDTO).toList();
    }

    @Override
    public Optional<PrescriptionDto> getPrescriptionById(UUID id) {
        return prescriptionRepository.findById(id)
                .map(PrescriptionDto::mapToDTO);
    }

    @Override
    public PrescriptionDto savePrescription(PrescriptionDto prescriptionDto) {
        Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Prescription prescription = PrescriptionDto.mapToEntity(prescriptionDto, patient);
        return PrescriptionDto.mapToDTO(prescriptionRepository.save(prescription));
    }

    @Override
    public PrescriptionDto updatePrescription(UUID id, PrescriptionDto prescriptionDto) {
        Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return prescriptionRepository.findById(id).map(prescription -> {
            prescription.setDosage(prescriptionDto.getDosage());
            prescription.setFrequency(prescriptionDto.getFrequency());
            prescription.setMedicationName(prescriptionDto.getMedicationName());
            prescription.setPatient(patient);
            prescription.setPrescribedBy(prescriptionDto.getPrescribedBy());
            prescription.setPrescriptionDate(prescriptionDto.getPrescriptionDate());
            return PrescriptionDto.mapToDTO(prescriptionRepository.save(prescription));
        }).orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

    }

    @Override
    public void deletePrescription(UUID id) {
        prescriptionRepository.deleteById(id);
    }
}
