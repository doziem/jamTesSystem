package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.User;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public DoctorDto toDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .fullName(doctor.getUser() != null ? doctor.getUser().getName(): null)
                .specialization(doctor.getSpecialization())
                .experience(doctor.getExperience())
                .email(doctor.getUser() != null ? doctor.getUser().getEmail() : null)
                .phoneNumber(doctor.getUser() != null ? doctor.getUser().getPhone() : null)
                .userId(doctor.getUser() != null ? doctor.getUser().getId() : null)
                .availability(doctor.getAvailability())
                .build();
    }

    public Doctor toEntity(DoctorDto dto, User user) {
        return Doctor.builder()
                .id(dto.getId())
                .specialization(dto.getSpecialization())
                .experience(dto.getExperience())
                .user(user)
                .availability(dto.getAvailability())
                .build();
    }
}
