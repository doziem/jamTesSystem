package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PharmacyRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements IPrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Override
    public List<PrescriptionDto> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionDto::mapToDTO)
                .toList();
    }

    @Override
    public Optional<PrescriptionDto> getPrescriptionById(String id) {
        return prescriptionRepository.findById(id)
                .map(PrescriptionDto::mapToDTO);
    }

    @Override
    public PrescriptionDto savePrescription(PrescriptionDto prescriptionDto) {
        Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Pharmacy pharmacy = prescriptionDto.getPharmacyId() == null ? null : pharmacyRepository.findById(prescriptionDto.getPharmacyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));

        Prescription prescription = PrescriptionDto.mapToEntity(prescriptionDto, patient, pharmacy);
        prescription.setStatus("PENDING_PHARMACY_REVIEW");
        return PrescriptionDto.mapToDTO(prescriptionRepository.save(prescription));
    }

    @Override
    public PrescriptionDto updatePrescription(String id, PrescriptionDto prescriptionDto) {
        Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Pharmacy pharmacy = prescriptionDto.getPharmacyId() == null ? null : pharmacyRepository.findById(prescriptionDto.getPharmacyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));

        return prescriptionRepository.findById(id).map(prescription -> {
            prescription.setDosage(prescriptionDto.getDosage());
            prescription.setFrequency(prescriptionDto.getFrequency());
            prescription.setMedicationName(prescriptionDto.getMedicationName());
            prescription.setPatient(patient);
            prescription.setPharmacy(pharmacy);
            prescription.setDepartment(prescriptionDto.getDepartment());
            prescription.setQuantity(prescriptionDto.getQuantity());
            prescription.setPrescribedBy(prescriptionDto.getPrescribedBy());
            prescription.setPrescriptionDate(prescriptionDto.getPrescriptionDate());
            prescription.setStatus(prescriptionDto.getStatus() != null ? prescriptionDto.getStatus() : prescription.getStatus());
            prescription.setPaymentConfirmed(prescriptionDto.isPaymentConfirmed());
            prescription.setTotalCost(prescriptionDto.getTotalCost() != null ? prescriptionDto.getTotalCost() : prescription.getTotalCost());
            return PrescriptionDto.mapToDTO(prescriptionRepository.save(prescription));
        }).orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
    }

    @Override
    public void deletePrescription(String id) {
        prescriptionRepository.deleteById(id);
    }
}
