package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.dto.DoctorDashboardDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.mapper.LabReportMapper;
import com.doziem.jamTesSystem.mapper.PrescriptionMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.LabReport;
import com.doziem.jamTesSystem.model.Prescription;
import com.doziem.jamTesSystem.model.User;
import com.doziem.jamTesSystem.repository.LabReportRepository;
import org.springframework.stereotype.Service;
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

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, DoctorMapper doctorMapper,
                             PrescriptionRepository prescriptionRepository, LabReportRepository labReportRepository,
                             PrescriptionMapper prescriptionMapper, LabReportMapper labReportMapper) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.doctorMapper = doctorMapper;
        this.prescriptionRepository = prescriptionRepository;
        this.labReportRepository = labReportRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.labReportMapper = labReportMapper;
    }

    @Override
    public DoctorDto createDoctor(DoctorDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorMapper.toEntity(dto, user);
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
    public DoctorDto updateDoctor(String id, DoctorDto dto) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
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
    public DoctorDashboardDto getDashboard(String doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        List<Prescription> prescriptions = prescriptionRepository.findAll().stream()
                .filter(prescription -> prescription.getPrescribedBy() != null && prescription.getPrescribedBy().equals(doctorId))
                .toList();

        List<LabReport> labReports = labReportRepository.findByLabRequestRequestedById(doctorId);

        return DoctorDashboardDto.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .totalPatients((int) prescriptions.stream()
                        .map(prescription -> prescription.getPatient() != null ? prescription.getPatient().getId() : null)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count())
                .totalPrescriptions(prescriptions.size())
                .pendingPrescriptions((int) prescriptions.stream()
                        .filter(prescription -> "PENDING_PHARMACY_REVIEW".equalsIgnoreCase(prescription.getStatus()))
                        .count())
                .pendingLabReports((int) labReports.stream()
                        .filter(labReport -> labReport.getResult() == null || labReport.getResult().isBlank())
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
}
