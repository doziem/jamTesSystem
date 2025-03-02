package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.User;
import org.springframework.stereotype.Service;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorServiceImpl implements IDoctorService{

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DoctorDto createDoctor(DoctorDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = DoctorDto.mapToEntity(dto, user);
        return DoctorDto.mapToDTO(doctorRepository.save(doctor));
    }

    @Override
    public List<DoctorDto> getAllDoctors() {

        return doctorRepository.findAll().stream()
                .map(DoctorDto::mapToDTO)
                .toList();
    }

    @Override
    public DoctorDto getDoctorById(UUID id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return DoctorDto.mapToDTO(doctor);
    }

    @Override
    public DoctorDto updateDoctor(UUID id, DoctorDto dto) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setExperience(dto.getExperience());
        doctor.setAvailability(dto.getAvailability());

        return DoctorDto.mapToDTO(doctorRepository.save(doctor));
    }

    @Override
    public void deleteDoctor(UUID id) {

        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }
}
