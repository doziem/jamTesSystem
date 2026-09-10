package com.doziem.jamTesSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncounterStatusRequest {
    private String status;
    private String departmentName;
    private String assignedDoctorId;
    private String triageNotes;
    private String admissionNotes;
    private String dischargeNotes;
}
