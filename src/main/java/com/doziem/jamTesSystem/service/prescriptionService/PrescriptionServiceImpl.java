package com.doziem.jamTesSystem.service.prescriptionService;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Medication;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Pharmacy;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.MedicationRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PharmacyRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    public List<PrescriptionDto> getAllPrescriptions() {
        return getAllPrescriptions(0, 10);
    }

    @Override
    public List<PrescriptionDto> getAllPrescriptions(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        return prescriptionRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(prescriptionMapper::toDto)
                .toList();
    }

    @Override
    public Optional<PrescriptionDto> getPrescriptionById(String id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toDto);
    }

    @Override
    public PrescriptionDto savePrescription(PrescriptionDto prescriptionDto) {
        return prescribeMedication(prescriptionDto, null);
    }

    @Override
    public PrescriptionDto prescribeMedication(PrescriptionDto prescriptionDto, Authentication authentication) {
        if (prescriptionDto == null) {
            throw new IllegalArgumentException("Prescription data is required");
        }
        if (prescriptionDto.getPatientId() == null || prescriptionDto.getPatientId().isBlank()) {
            throw new IllegalArgumentException("Patient ID is required");
        }
        if (prescriptionDto.getMedicationName() == null || prescriptionDto.getMedicationName().isBlank()) {
            throw new IllegalArgumentException("Medication name is required");
        }
        if (prescriptionDto.getPrescribedBy() == null || prescriptionDto.getPrescribedBy().isBlank()) {
            throw new IllegalArgumentException("Prescribing doctor is required");
        }

        User doctor = userRepository.findById(prescriptionDto.getPrescribedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new UserNotAllowedException("Only doctors can prescribe medication");
        }

        if (authentication != null && isCurrentDoctor(authentication, doctor.getId())) {
            throw new UserNotAllowedException("You are not allowed to prescribe for this doctor account");
        }

        Medication medication = medicationRepository.findByNameIgnoreCase(prescriptionDto.getMedicationName())
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + prescriptionDto.getMedicationName()));

        Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Pharmacy pharmacy = prescriptionDto.getPharmacyId() == null ? null : pharmacyRepository.findById(prescriptionDto.getPharmacyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));

        Prescription prescription = prescriptionMapper.toEntity(prescriptionDto, patient, pharmacy);
        prescription.setPrescribedBy(doctor.getId());
        prescription.setMedicationName(medication.getName());
        prescription.setStatus("PENDING_PHARMACY_REVIEW");
        return prescriptionMapper.toDto(prescriptionRepository.save(prescription));
    }

    private boolean isCurrentDoctor(Authentication authentication, String doctorId) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return true;
        }
        if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return true;
        }

        return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .map(user -> user.getId().equals(doctorId) || user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    @Override
    public PrescriptionDto updatePrescription(String id, PrescriptionDto prescriptionDto, Authentication authentication) {
        if (prescriptionDto == null) {
            throw new IllegalArgumentException("Prescription data is required");
        }

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        if (authentication != null && isCurrentDoctor(authentication, prescription.getPrescribedBy())) {
            throw new UserNotAllowedException("You are not allowed to update this prescription");
        }

        if (prescriptionDto.getPatientId() != null && !prescriptionDto.getPatientId().isBlank()) {
            Patient patient = patientRepository.findById(prescriptionDto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            prescription.setPatient(patient);
        }

        if (prescriptionDto.getPharmacyId() != null) {
            Pharmacy pharmacy = pharmacyRepository.findById(prescriptionDto.getPharmacyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pharmacy not found"));
            prescription.setPharmacy(pharmacy);
        }

        if (prescriptionDto.getMedicationName() != null && !prescriptionDto.getMedicationName().isBlank()) {
            Medication medication = medicationRepository.findByNameIgnoreCase(prescriptionDto.getMedicationName())
                    .orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + prescriptionDto.getMedicationName()));
            prescription.setMedicationName(medication.getName());
        }

        if (prescriptionDto.getDosage() != null && !prescriptionDto.getDosage().isBlank()) {
            prescription.setDosage(prescriptionDto.getDosage());
        }
        if (prescriptionDto.getFrequency() != null && !prescriptionDto.getFrequency().isBlank()) {
            prescription.setFrequency(prescriptionDto.getFrequency());
        }
        if (prescriptionDto.getDepartment() != null) {
            prescription.setDepartment(prescriptionDto.getDepartment());
        }
        if (prescriptionDto.getQuantity() > 0) {
            prescription.setQuantity(prescriptionDto.getQuantity());
        }
        if (prescriptionDto.getPrescriptionDate() != null && !prescriptionDto.getPrescriptionDate().isBlank()) {
            prescription.setPrescriptionDate(prescriptionDto.getPrescriptionDate());
        }
        if (prescriptionDto.getStatus() != null && !prescriptionDto.getStatus().isBlank()) {
            prescription.setStatus(prescriptionDto.getStatus());
        }
        if (prescriptionDto.getTotalCost() != null) {
            prescription.setTotalCost(prescriptionDto.getTotalCost());
        }

        return prescriptionMapper.toDto(prescriptionRepository.save(prescription));
    }

    @Override
    public void deletePrescription(String id) {
        prescriptionRepository.deleteById(id);
    }
}
