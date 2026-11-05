package com.doziem.jamTesSystem.service.doctorService;

import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.dto.DoctorDashboardDto;
import com.doziem.jamTesSystem.dto.DoctorPatientVisitDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IDoctorService {

    DoctorDto createDoctor(DoctorDto dto);

    List<DoctorDto> getAllDoctors();

    List<DoctorDto> getAllDoctors(int page, int size);

    DoctorDto getDoctorById(String id);

    DoctorDto getCurrentDoctor(Authentication authentication);

    DoctorDto updateDoctor(String id, DoctorDto dto);

    void deleteDoctor(String id);

    DoctorDashboardDto getDashboard(String doctorId);

    List<DoctorPatientVisitDto> getAssignedPatients(String doctorId);

    List<DoctorPatientVisitDto> getAllAssignedPatients();
}
