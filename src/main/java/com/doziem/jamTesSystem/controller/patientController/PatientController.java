package com.doziem.jamTesSystem.controller.patientController;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.patientService.IPatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    // Create a new patient
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createPatient(@RequestBody PatientDto patientDTO) {
        PatientDto response = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Patient Successfully Created",response));
    }

    // Get a patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPatientById(@PathVariable UUID id) {

        return ResponseEntity.ok().body(new ApiResponse(true, "User fetched", patientService.getPatientById(id)) );
    }

    // Get all patients
    @GetMapping("/all")
    public ResponseEntity<List<ApiResponse>> getAllPatients() {
        return ResponseEntity.ok().body(Collections.singletonList(new ApiResponse(true, "All Patient Fetched", patientService.getAllPatients())));
    }

    // Update a patient
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePatient(@PathVariable UUID id, @RequestBody PatientDto patientDtO) {
        return ResponseEntity.ok().body(new ApiResponse(true, "User fetched", patientService.updatePatient(id, patientDtO)));
    }

    // Delete a patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
