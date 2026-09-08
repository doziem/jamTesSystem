package com.doziem.jamTesSystem.controller.doctorController;

import com.doziem.jamTesSystem.config.JwtAuthenticationFilter;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.dto.DoctorDashboardDto;
import com.doziem.jamTesSystem.dto.DoctorDto;
import com.doziem.jamTesSystem.service.doctorService.IDoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDoctorService doctorService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void createDoctorReturnsCreatedDoctor() throws Exception {
        when(doctorService.createDoctor(any(DoctorDto.class)))
                .thenReturn(DoctorDto.builder().id("d-1").firstName("John").lastName("Doe").build());

        mockMvc.perform(post("/api/doctors/register-doctor")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
        .andExpect(status().isOk());
    }

    @Test
    void getAllDoctorsReturnsList() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(List.of(DoctorDto.builder().id("d-1").firstName("John").build()));

        mockMvc.perform(get("/api/doctors/all").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getDoctorByIdReturnsDoctor() throws Exception {
        when(doctorService.getDoctorById("d-1")).thenReturn(DoctorDto.builder().id("d-1").firstName("John").build());

        mockMvc.perform(get("/api/doctors/d-1/single").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void updateDoctorReturnsUpdatedDoctor() throws Exception {
        when(doctorService.updateDoctor(eq("d-1"), any(DoctorDto.class)))
                .thenReturn(DoctorDto.builder().id("d-1").firstName("Jane").build());

        mockMvc.perform(put("/api/doctors/d-1/update")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDoctorReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/doctors/d-1/delete").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getDoctorDashboardReturnsSummary() throws Exception {
        when(doctorService.getDashboard("d-1")).thenReturn(DoctorDashboardDto.builder()
                .doctorId("d-1")
                .doctorName("John Doe")
                .specialization("General Practice")
                .totalPrescriptions(4)
                .pendingLabReports(2)
                .build());

        mockMvc.perform(get("/api/doctors/d-1/dashboard").with(user("doctor").roles("DOCTOR")))
                .andExpect(status().isOk());
    }
}
