package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.mapper.DoctorMapper;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.User;
import org.springframework.stereotype.Service;
import com.doziem.jamTesSystem.repository.DoctorRepository;
import com.doziem.jamTesSystem.repository.UserRepository;

import java.util.List;

@Service
public class DoctorServiceImpl implements IDoctorService{

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.doctorMapper = doctorMapper;
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
}
