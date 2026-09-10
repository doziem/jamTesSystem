package com.doziem.jamTesSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String specialization;
    private int experience;
    private String userId;
    private String availability;

    public DoctorDto(String id, String firstName, String lastName, String specialization, int experience, String userId, String availability) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        this.specialization = specialization;
        this.experience = experience;
        this.userId = userId;
        this.availability = availability;
    }


}
