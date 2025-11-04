package com.doziem.jamTesSystem.controller.labReportController;

import com.doziem.jamTesSystem.dto.LabReportDto;
import com.doziem.jamTesSystem.exceptions.ResourceNotFoundException;
import com.doziem.jamTesSystem.exceptions.UserNotAllowedException;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.labReportService.ILabReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lab-reports")
public class LabReportController {

    private final ILabReportService labReportService;

    public LabReportController(ILabReportService labReportService) {
        this.labReportService = labReportService;
    }

    // Create a new lab report
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createLabReport(@RequestBody LabReportDto dto) {
        try {
            return ResponseEntity.ok().body(new ApiResponse(true,"Report Successfully Created" ,labReportService.createLabReport(dto)));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        }catch (UserNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage()));
        }

    }

    // Get all lab reports
    @GetMapping("/patients/all")
    public ResponseEntity<List<LabReportDto>> getAllLabReports() {
        return ResponseEntity.ok(labReportService.getAllLabReports());
    }

    // Get a lab report by ID
    @GetMapping("/{id}")
    public ResponseEntity<LabReportDto> getLabReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(labReportService.getLabReportById(id));
    }

    // Get all lab reports by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabReportDto>> getLabReportsByPatientId(@PathVariable UUID patientId) {
        return ResponseEntity.ok(labReportService.getLabReportsByPatientId(patientId));
    }

    // Update a lab report
    @PutMapping("/{id}")
    public ResponseEntity<LabReportDto> updateLabReport(@PathVariable UUID id, @RequestBody LabReportDto dto) {
        return ResponseEntity.ok(labReportService.updateLabReport(id, dto));
    }

    // Delete a lab report
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabReport(@PathVariable UUID id) {
        labReportService.deleteLabReport(id);
        return ResponseEntity.noContent().build();
    }
}
