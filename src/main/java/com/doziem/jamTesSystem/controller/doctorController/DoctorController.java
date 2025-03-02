package com.doziem.jamTesSystem.controller.doctorController;


import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.service.doctorService.IDoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final IDoctorService doctorService;

    public DoctorController(IDoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}/single")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable UUID id, @RequestBody DoctorDto dto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


}
