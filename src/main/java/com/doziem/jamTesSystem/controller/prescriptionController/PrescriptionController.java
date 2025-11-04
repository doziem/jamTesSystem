package com.doziem.jamTesSystem.controller.prescriptionController;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.model.Patient;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.prescriptionService.IPrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private IPrescriptionService prescriptionService;

    @GetMapping("/all")
    public List<PrescriptionDto> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable UUID id) {
        return prescriptionService.getPrescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPrescription(@RequestBody PrescriptionDto prescriptionDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Prescription Created", prescriptionService.savePrescription(prescriptionDto)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable UUID id, @RequestBody PrescriptionDto prescriptionDto) {
        try {
            return ResponseEntity.ok(prescriptionService.updatePrescription(id, prescriptionDto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable UUID id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
