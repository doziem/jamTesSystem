package com.doziem.jamTesSystem.request;

import com.doziem.jamTesSystem.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientArrivalRequest {
    private String mrn;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private Address address;
    private String visitType;
    private String departmentName;
    private String assignedDoctorId;
    private String triageNotes;
}
