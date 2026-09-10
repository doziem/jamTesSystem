package com.doziem.jamTesSystem.service.patientService;

import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.exceptions.InvalidResourceException;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.BillingMapper;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.mapper.EncounterMapper;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.mapper.PatientMapper;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.Encounter;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.repository.BillingRepository;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.EncounterRepository;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import com.doziem.jamTesSystem.repository.PatientRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.request.EncounterStatusRequest;
import com.doziem.jamTesSystem.request.PatientArrivalRequest;
import com.doziem.jamTesSystem.response.PatientArrivalResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PatienceService implements IPatientService{
    private final PatientRepository patientRepository;
    private final EncounterRepository encounterRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;
    private final BillingRepository billingRepository;
    private final DoctorMapper doctorMapper;
    private final BillingMapper billingMapper;
    private final LabReportMapper labReportMapper;
    private final PrescriptionMapper prescriptionMapper;
    private PatientMapper patientMapper = new PatientMapper(
            new BillingMapper(),
            new LabReportMapper(),
            new PrescriptionMapper(),
            new EncounterMapper());
    private EncounterMapper encounterMapper = new EncounterMapper();

    @Override
    public PatientDto createPatient(PatientDto patientDto) {
        Patient patient = patientMapper.toEntity(patientDto, new Patient());
        if (patient.getMrn() == null || patient.getMrn().isBlank()) {
            patient.setMrn(generateUniqueMrn());
        }
        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        PatientDto patientDto = patientMapper.toDto(patient);
        patientDto.setEncounters(getPatientVisitHistory(id));
        patientDto.setPrescriptions(prescriptionRepository == null || prescriptionMapper == null
                ? new ArrayList<>()
                : prescriptionRepository.findByPatientId(id).stream().map(prescriptionMapper::toDto).collect(Collectors.toList()));
        patientDto.setLabReports(labReportRepository == null || labReportMapper == null
                ? new ArrayList<>()
                : labReportRepository.findByPatientId(id).stream().map(labReportMapper::toDto).collect(Collectors.toList()));
        patientDto.setBillingRecords(billingRepository == null || billingMapper == null
                ? new ArrayList<>()
                : billingRepository.findByPatientId(id).stream().map(billingMapper::toDto).collect(Collectors.toList()));
        return patientDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto getPatientByMrn(String mrn) {
        return patientRepository.findByMrn(mrn)
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with MRN: " + mrn));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatients(String mrn, String phone, String name, String dob) {
        if (mrn != null && !mrn.isBlank()) {
            return patientRepository.findByMrn(mrn.trim())
                   .map(List::of)
                   .orElse(List.of())
                   .stream()
                   .map(patientMapper::toDto)
                   .collect(Collectors.toList());
        }

        if (phone != null && !phone.isBlank()) {
            return patientRepository.findByPhone(phone.trim())
                   .map(List::of)
                   .orElse(List.of())
                   .stream()
                   .map(patientMapper::toDto)
                   .collect(Collectors.toList());
        }

        if (name != null && !name.isBlank() || dob != null && !dob.isBlank()) {
            String normalizedName = name == null ? "" : name.trim().toLowerCase();
            LocalDate parsedDob = dob == null || dob.isBlank() ? null : LocalDate.parse(dob);
            return patientRepository.findAll().stream()
                   .filter(patient -> {
                       boolean nameMatches = normalizedName.isBlank() || (
                               patient.getFirstName() != null && patient.getFirstName().toLowerCase().contains(normalizedName)
                                       || patient.getLastName() != null && patient.getLastName().toLowerCase().contains(normalizedName));
                       boolean dobMatches = parsedDob == null || Objects.equals(patient.getDateOfBirth(), parsedDob);
                       return nameMatches && dobMatches;
                   })
                   .map(patientMapper::toDto)
                   .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public PatientArrivalResponse arrivePatient(PatientArrivalRequest request) {
        if (request == null) {
            throw new InvalidResourceException("Arrival request is required");
        }

        Patient patient = null;
        if (request.getMrn() != null && !request.getMrn().isBlank()) {
            patient = patientRepository.findByMrn(request.getMrn().trim()).orElse(null);
        }
        if (patient == null && request.getPhone() != null && !request.getPhone().isBlank()) {
            patient = patientRepository.findByPhone(request.getPhone().trim()).orElse(null);
        }

        if (patient == null) {
            PatientDto patientDto = new PatientDto();
            patientDto.setMrn(request.getMrn() != null && !request.getMrn().isBlank() ? request.getMrn().trim() : generateUniqueMrn());
            patientDto.setFirstName(request.getFirstName());
            patientDto.setLastName(request.getLastName());
            patientDto.setEmail(request.getEmail());
            patientDto.setPhone(request.getPhone());
            patientDto.setDateOfBirth(request.getDateOfBirth());
            patientDto.setGender(request.getGender());
            patientDto.setAddress(request.getAddress());
            patientDto.setActive(true);
            patient = patientMapper.toEntity(createPatient(patientDto), new Patient());
        }

        Encounter encounter = getEncounter(request, patient);
        Encounter savedEncounter = encounterRepository.save(encounter);

        return PatientArrivalResponse.builder()
                .patient(patientMapper.toDto(patient))
                .encounter(encounterMapper.toDto(savedEncounter))
                .message("Patient checked in successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> getAssignableDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toList());
    }

    private Encounter getEncounter(PatientArrivalRequest request, Patient patient) {
        String visitType = request.getVisitType() == null || request.getVisitType().isBlank() ? "OPD" : request.getVisitType().trim();
        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setVisitType(visitType);
        encounter.setStatus("ARRIVED");
        encounter.setDepartmentName(request.getDepartmentName() == null || request.getDepartmentName().isBlank() ? "Reception" : request.getDepartmentName());
        applyDoctorAssignment(encounter, request.getAssignedDoctorId());
        encounter.setTriageNotes(request.getTriageNotes());
        return encounter;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterDto> getPatientVisitHistory(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidResourceException("Patient id is required");
        }

        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return encounterRepository.findByPatientIdOrderByArrivalTimeDesc(patientId)
                .stream()
                .map(encounterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EncounterDto updateEncounterStatus(String encounterId, EncounterStatusRequest request) {
        if (encounterId == null || encounterId.isBlank()) {
            throw new InvalidResourceException("Encounter id is required");
        }
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new InvalidResourceException("Status is required");
        }

        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found"));

        String nextStatus = normalizeEncounterStatus(request.getStatus());
        validateEncounterStatusTransition(encounter.getStatus(), nextStatus);
        if (request.getDepartmentName() != null && !request.getDepartmentName().isBlank()) {
            encounter.setDepartmentName(request.getDepartmentName().trim());
        }
        if (request.getTriageNotes() != null && !request.getTriageNotes().isBlank()) {
            encounter.setTriageNotes(request.getTriageNotes().trim());
        }
        if (request.getAdmissionNotes() != null && !request.getAdmissionNotes().isBlank()) {
            encounter.setAdmissionNotes(request.getAdmissionNotes().trim());
        }
        if (request.getDischargeNotes() != null && !request.getDischargeNotes().isBlank()) {
            encounter.setDischargeNotes(request.getDischargeNotes().trim());
        }
        if (request.getAssignedDoctorId() != null) {
            applyDoctorAssignment(encounter, request.getAssignedDoctorId());
        }
        encounter.setStatus(nextStatus);
        if ("ADMITTED".equals(nextStatus) && encounter.getAdmittedAt() == null) {
            encounter.setAdmittedAt(java.time.LocalDateTime.now());
        }
        if ("DISCHARGED".equals(nextStatus) && encounter.getDischargedAt() == null) {
            encounter.setDischargedAt(java.time.LocalDateTime.now());
        }
        return encounterMapper.toDto(encounterRepository.save(encounter));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        return patientRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDto updatePatient(String id, PatientDto patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Patient updatedPatient = patientRepository.save(updateExistingPatient(existingPatient,patientDTO));
        return patientMapper.toDto(updatedPatient);
    }

    private Patient updateExistingPatient(Patient existingPatient,PatientDto patientDto) {
        existingPatient.setMrn(patientDto.getMrn() != null ? patientDto.getMrn() : existingPatient.getMrn());
        existingPatient.setFirstName(patientDto.getFirstName() != null ? patientDto.getFirstName() : existingPatient.getFirstName());
        existingPatient.setLastName(patientDto.getLastName() != null ? patientDto.getLastName() : existingPatient.getLastName());
        existingPatient.setEmail(patientDto.getEmail() != null ? patientDto.getEmail() : existingPatient.getEmail());
        existingPatient.setPhone(patientDto.getPhone() != null ? patientDto.getPhone() : existingPatient.getPhone());
        existingPatient.setDateOfBirth(patientDto.getDateOfBirth() != null ? patientDto.getDateOfBirth() : existingPatient.getDateOfBirth());
        existingPatient.setGender(patientDto.getGender() != null ? patientDto.getGender() : existingPatient.getGender());
        existingPatient.setAddress(patientDto.getAddress() != null ? patientDto.getAddress() : existingPatient.getAddress());
        existingPatient.setActive(patientDto.isActive());
        return existingPatient;
    }

    @Override
    public void deletePatient(String id) {
      Patient patient=  patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);

    }

    private void applyDoctorAssignment(Encounter encounter, String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            encounter.setAssignedDoctorId(null);
            encounter.setAssignedDoctorName(null);
            return;
        }

        Doctor doctor = doctorRepository.findById(doctorId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        encounter.setAssignedDoctorId(doctor.getId());
        encounter.setAssignedDoctorName(((doctor.getFirstName() == null ? "" : doctor.getFirstName()) + " "
                + (doctor.getLastName() == null ? "" : doctor.getLastName())).trim());
    }

    private String normalizeEncounterStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new InvalidResourceException("Status is required");
        }

        String normalized = rawStatus.trim().toUpperCase().replace(' ', '_');
        String compact = normalized.replace('-', '_');

        return switch (compact) {
            case "ARRIVED", "CHECKED_IN" -> "ARRIVED";
            case "TRIAGED", "IN_TRIAGE" -> "TRIAGED";
            case "ADMITTED", "ADMISSION" -> "ADMITTED";
            case "IN_TREATMENT", "TREATMENT" -> "IN_TREATMENT";
            case "DISCHARGED", "DISCHARGE" -> "DISCHARGED";
            default -> throw new InvalidResourceException("Unsupported encounter status: " + rawStatus);
        };
    }

    private void validateEncounterStatusTransition(String currentStatus, String nextStatus) {
        String normalizedCurrent = normalizeEncounterStatus(currentStatus);
        if (normalizedCurrent.equals(nextStatus)) {
            return;
        }

        List<String> workflow = Arrays.asList("ARRIVED", "TRIAGED", "ADMITTED", "IN_TREATMENT", "DISCHARGED");
        int currentIndex = workflow.indexOf(normalizedCurrent);
        int nextIndex = workflow.indexOf(nextStatus);

        if (currentIndex == -1 || nextIndex == -1) {
            throw new InvalidResourceException("Unsupported encounter status transition");
        }

        if (nextIndex != currentIndex + 1) {
            throw new InvalidResourceException(
                    "Encounter status can only move from " + normalizedCurrent + " to "
                            + (currentIndex + 1 < workflow.size() ? workflow.get(currentIndex + 1) : normalizedCurrent)
            );
        }
    }

    private String generateUniqueMrn() {
        int attempts = 0;
        while (attempts < 10) {
            int number = ThreadLocalRandom.current().nextInt(100000, 999999);
            String candidate = "JAM-" + Year.now().getValue() + "-" + number;
            if (!patientRepository.existsByMrn(candidate)) {
                return candidate;
            }
            attempts++;
        }
        throw new IllegalStateException("Unable to generate a unique MRN for patient");
    }
} 
