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
    public ResponseEntity<LabReportDto> getLabReportById(@PathVariable String id) {
        return ResponseEntity.ok(labReportService.getLabReportById(id));
    }

    // Get all lab reports by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabReportDto>> getLabReportsByPatientId(@PathVariable String patientId) {
        return ResponseEntity.ok(labReportService.getLabReportsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<LabReportDto>> getLabReportsByRequestedBy(@PathVariable String doctorId) {
        return ResponseEntity.ok(labReportService.getLabReportsByRequestedBy(doctorId));
    }

    // Update a lab report
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLabReport(@PathVariable String id, @RequestBody LabReportDto dto) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Lab report updated", labReportService.updateLabReport(id, dto)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
        } catch (UserNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Delete a lab report
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabReport(@PathVariable String id) {
        labReportService.deleteLabReport(id);
        return ResponseEntity.noContent().build();
    }
}
