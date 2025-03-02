package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;

import java.util.List;
import java.util.UUID;

public interface IDoctorService {

    DoctorDto createDoctor(DoctorDto dto);

    List<DoctorDto> getAllDoctors();

    DoctorDto getDoctorById(UUID id);

    DoctorDto updateDoctor(UUID id, DoctorDto dto);

    void deleteDoctor(UUID id);
}
