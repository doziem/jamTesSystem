package com.doziem.jamTesSystem.controller.medicationController;

import com.doziem.jamTesSystem.dto.MedicationDto;
import com.doziem.jamTesSystem.service.medication.IMedicationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MedicationController {
    private final IMedicationService medicationService;

    @PostMapping("/create/{supervisorId}")
    public MedicationDto createMedication(@RequestBody @Valid MedicationDto medicationDto, @PathVariable String supervisorId) {
        return medicationService.saveMedication(medicationDto, supervisorId);
    }
    public List<MedicationDto> getAllMedications() {
        return medicationService.getAllMedications();
    }

}
