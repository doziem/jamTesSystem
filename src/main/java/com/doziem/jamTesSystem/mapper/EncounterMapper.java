package com.doziem.jamTesSystem.mapper;

import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.model.Encounter;
import org.springframework.stereotype.Component;

@Component
public class EncounterMapper {

    public EncounterDto toDto(Encounter encounter) {
        if (encounter == null) {
            return null;
        }

        return EncounterDto.builder()
                .id(encounter.getId())
                .patientId(encounter.getPatient() != null ? encounter.getPatient().getId() : null)
                .visitType(encounter.getVisitType())
                .status(encounter.getStatus())
                .arrivalTime(encounter.getArrivalTime())
                .departmentName(encounter.getDepartmentName())
                .assignedDoctorId(encounter.getAssignedDoctorId())
                .triageNotes(encounter.getTriageNotes())
                .build();
    }
}
