package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.dto.DoctorDashboardDto;
import com.doziem.jamTesSystem.dto.DoctorPatientVisitDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.Encounter;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.EncounterRepository;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.PrescriptionRepository;
import com.doziem.jamTesSystem.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class DoctorServiceImpl implements IDoctorService{

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;
    private final PrescriptionRepository prescriptionRepository;
    private final LabReportRepository labReportRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final LabReportMapper labReportMapper;
    private final EncounterRepository encounterRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, DoctorMapper doctorMapper,
                             PrescriptionRepository prescriptionRepository, LabReportRepository labReportRepository,
                             PrescriptionMapper prescriptionMapper, LabReportMapper labReportMapper,
                             EncounterRepository encounterRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.doctorMapper = doctorMapper;
        this.prescriptionRepository = prescriptionRepository;
        this.labReportRepository = labReportRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.labReportMapper = labReportMapper;
        this.encounterRepository = encounterRepository;
    }

    @Override
    public DoctorDto createDoctor(DoctorDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        Doctor doctor = doctorMapper.toEntity(dto, user);
        doctor.setFullName(user.getName());
        doctor.setEmail(user.getEmail());
        doctor.setPhoneNumber(user.getPhone());
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        return getAllDoctors(0, 10);
    }

    @Override
    public List<DoctorDto> getAllDoctors(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        return doctorRepository.findAll().stream()
                .skip((long) page * size)
                .limit(size)
                .map(doctorMapper::toDto)
                .toList();
    }

    @Override
    public DoctorDto getDoctorById(String id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return doctorMapper.toDto(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto getCurrentDoctor(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new ResourceNotFoundException("Doctor profile not found for current user");
        }

        User user = userRepository.findByEmailOrPhone(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));

        return doctorMapper.toDto(doctor);
    }

    @Override
    public DoctorDto updateDoctor(String id, DoctorDto dto) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperience(dto.getExperience());
        doctor.setAvailability(dto.getAvailability());

        return doctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Override
    public void deleteDoctor(String id) {

        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDashboardDto getDashboard(String doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        String doctorUserId = doctor.getUser() != null ? doctor.getUser().getId() : null;
        List<Prescription> prescriptions = prescriptionRepository.findAll().stream()
                .filter(prescription -> doctorUserId != null && doctorUserId.equals(prescription.getPrescribedBy()))
                .toList();

        List<LabReport> labReports = doctorUserId == null ? List.of() : labReportRepository.findByLabRequestRequestedById(doctorUserId);

        return DoctorDashboardDto.builder()
                .doctorId(doctor.getId() != null ? doctor.getId().toString() : null)
                .doctorName(doctor.getUser() != null ? doctor.getUser().getName() : doctor.getFullName())
                .specialization(doctor.getSpecialization())
                .totalPatients((int) prescriptions.stream()
                        .map(prescription -> prescription.getPatient() != null && prescription.getPatient().getId() != null ? prescription.getPatient().getId().toString() : null)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count())
                .totalPrescriptions(prescriptions.size())
                .pendingPrescriptions((int) prescriptions.stream()
                        .filter(prescription -> "PENDING_PHARMACY_REVIEW".equalsIgnoreCase(prescription.getStatus()))
                        .count())
                .pendingLabReports((int) labReports.stream()
                        .filter(labReport -> labReport.getTestResults() == null
                                || labReport.getTestResults().isEmpty()
                                || labReport.getTestResults().stream()
                                .anyMatch(result -> result.getResult() == null || result.getResult().isBlank()))
                        .count())
                .recentPrescriptions(prescriptions.stream()
                        .sorted((a, b) -> {
                            String left = a.getPrescriptionDate() == null ? "" : a.getPrescriptionDate();
                            String right = b.getPrescriptionDate() == null ? "" : b.getPrescriptionDate();
                            return right.compareTo(left);
                        })
                        .limit(5)
                        .map(prescriptionMapper::toDto)
                        .toList())
                .recentLabReports(labReports.stream()
                        .sorted((a, b) -> {
                            String left = a.getReportDate() == null ? "" : a.getReportDate().toString();
                            String right = b.getReportDate() == null ? "" : b.getReportDate().toString();
                            return right.compareTo(left);
                        })
                        .limit(5)
                        .map(labReportMapper::toDto)
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorPatientVisitDto> getAssignedPatients(String doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found");
        }

        return encounterRepository.findByAssignedDoctorIdOrderByArrivalTimeDesc(doctorId).stream()
                .map(this::toPatientVisitDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorPatientVisitDto> getAllAssignedPatients() {
        return encounterRepository.findByAssignedDoctorIdIsNotNullOrderByArrivalTimeDesc().stream()
                .map(this::toPatientVisitDto)
                .toList();
    }

    private DoctorPatientVisitDto toPatientVisitDto(Encounter encounter) {
        Patient patient = encounter.getPatient();
        return DoctorPatientVisitDto.builder()
                .encounterId(encounter.getId())
                .patientId(patient != null ? patient.getId() : null)
                .mrn(patient != null ? patient.getMrn() : null)
                .patientName(patient != null ? (patient.getFirstName() + " " + patient.getLastName()).trim() : null)
                .phone(patient != null ? patient.getPhone() : null)
                .gender(patient != null ? patient.getGender() : null)
                .dateOfBirth(patient != null ? patient.getDateOfBirth() : null)
                .visitType(encounter.getVisitType())
                .status(encounter.getStatus())
                .departmentName(encounter.getDepartmentName())
                .arrivalTime(encounter.getArrivalTime())
                .triageNotes(encounter.getTriageNotes())
                .assignedDoctorId(encounter.getAssignedDoctorId())
                .assignedDoctorName(encounter.getAssignedDoctorName())
                .build();
    }
}
