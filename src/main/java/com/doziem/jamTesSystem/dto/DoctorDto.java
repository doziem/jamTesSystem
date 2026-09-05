package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Builder
@Data
public class DoctorDto {

    private String id;
    private String firstName;
    private String lastName;
    private String specialization;
    private int experience;
    private String userId;
    private String availability;

    public DoctorDto() {}

    public DoctorDto(String id, String firstName, String lastName, String specialization, int experience, String userId, String availability) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.experience = experience;
        this.userId = userId;
        this.availability = availability;
    }

    public static Doctor mapToEntity(DoctorDto dto, User user) {
        return new Doctor(
                dto.getId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getSpecialization(),
                dto.getExperience(),
                user,
                dto.getAvailability());
    }

    public static DoctorDto mapToDTO(Doctor doctor) {
        return new DoctorDto(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                doctor.getExperience(),
                doctor.getUser().getId(),
                doctor.getAvailability());
    }

}
