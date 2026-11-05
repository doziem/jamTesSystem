package com.doziem.jamTesSystem.controller.medicationController;

import com.doziem.jamTesSystem.dto.MedicationDto;
import com.doziem.jamTesSystem.service.medication.IMedicationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/medications")
public class MedicationController {
    private final IMedicationService medicationService;

    @PostMapping("/create/{supervisorId}")
    public MedicationDto createMedication(@RequestBody @Valid MedicationDto medicationDto, @PathVariable String supervisorId) {
        return medicationService.saveMedication(medicationDto, supervisorId);
    }

    @GetMapping
    public List<MedicationDto> getAllMedications() {
        return medicationService.getAllMedications();
    }

    @PutMapping("/{id}")
    public MedicationDto updateMedication(@PathVariable String id, @RequestBody @Valid MedicationDto medicationDto) {
        return medicationService.updateMedication(id, medicationDto);
    }

}
