package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Doctor;
import com.doziem.jamTesSystem.model.User;

import java.util.UUID;

public class DoctorDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private int experience;
    private UUID userId;
    private String availability; // Example: "Monday-Friday, 9 AM - 5 PM"

    public DoctorDto() {}

    public DoctorDto(UUID id, String firstName, String lastName, String specialization, int experience, UUID userId, String availability) {
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
