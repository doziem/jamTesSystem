package com.doziem.jamTesSystem.controller.prescriptionController;

import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.prescriptionService.IPrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable String id) {
        return prescriptionService.getPrescriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse> createPrescription(@RequestBody PrescriptionDto prescriptionDto, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Prescription Created", prescriptionService.prescribeMedication(prescriptionDto, authentication)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'SUPERVISOR', 'ADMIN')")
    public List<PrescriptionDto> getPharmacyPrescriptions(@PathVariable String pharmacyId) {
        return prescriptionService.getPharmacyPrescriptions(pharmacyId);
    }

    @PostMapping("/pharmacy/{pharmacyId}/{prescriptionId}/estimate")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> estimatePrescription(@PathVariable String pharmacyId, @PathVariable String prescriptionId) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Prescription estimated",
                    prescriptionService.estimatePrescription(pharmacyId, prescriptionId)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable String id, @RequestBody PrescriptionDto prescriptionDto, Authentication authentication) {
        try {
            return ResponseEntity.ok(prescriptionService.updatePrescription(id, prescriptionDto, authentication));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
