package com.doziem.jamTesSystem.controller.patientController;

import com.doziem.jamTesSystem.dto.EncounterDto;
import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.request.EncounterStatusRequest;
import com.doziem.jamTesSystem.request.PatientArrivalRequest;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.response.PatientArrivalResponse;
import com.doziem.jamTesSystem.service.patientService.IPatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createPatient(@RequestBody PatientDto patientDTO) {
        PatientDto response = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Patient Successfully Created", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchPatients(
            @RequestParam(required = false) String mrn,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dob) {
        List<PatientDto> patients = patientService.searchPatients(mrn, phone, name, dob);
        return ResponseEntity.ok(new ApiResponse(true, "Patients fetched", patients));
    }

    @PostMapping("/arrival")
    public ResponseEntity<ApiResponse> arrivePatient(@RequestBody PatientArrivalRequest request) {
        PatientArrivalResponse response = patientService.arrivePatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, response.getMessage(), response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok().body(new ApiResponse(true, "User fetched", patientService.getPatientById(id)) );
    }

    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<ApiResponse> getPatientByMrn(@PathVariable String mrn) {
        return ResponseEntity.ok().body(new ApiResponse(true, "Patient fetched", patientService.getPatientByMrn(mrn)));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse> getPatientVisitHistory(@PathVariable String id) {
        List<EncounterDto> history = patientService.getPatientVisitHistory(id);
        return ResponseEntity.ok(new ApiResponse(true, "Patient visit history fetched", history));
    }

    @PatchMapping("/encounters/{encounterId}/status")
    public ResponseEntity<ApiResponse> updateEncounterStatus(
            @PathVariable String encounterId,
            @RequestBody EncounterStatusRequest request) {
        EncounterDto updated = patientService.updateEncounterStatus(encounterId, request);
        return ResponseEntity.ok(new ApiResponse(true, "Encounter status updated", updated));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ApiResponse>> getAllPatients(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(Collections.singletonList(new ApiResponse(true, "All Patient Fetched", patientService.getAllPatients(page, size))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePatient(@PathVariable String id, @RequestBody PatientDto patientDtO) {
        return ResponseEntity.ok().body(new ApiResponse(true, "User fetched", patientService.updatePatient(id, patientDtO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
