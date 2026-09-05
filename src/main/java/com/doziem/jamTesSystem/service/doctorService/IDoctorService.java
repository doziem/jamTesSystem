package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;

import java.util.List;

public interface IDoctorService {

    DoctorDto createDoctor(DoctorDto dto);

    List<DoctorDto> getAllDoctors();

    DoctorDto getDoctorById(String id);

    DoctorDto updateDoctor(String id, DoctorDto dto);

    void deleteDoctor(String id);
}
