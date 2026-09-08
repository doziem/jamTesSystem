package com.doziem.jamTesSystem.controller.prescriptionController;

import com.doziem.jamTesSystem.config.JwtAuthenticationFilter;
import com.doziem.jamTesSystem.config.JwtUtil;
import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.dto.PrescriptionDto;
import com.doziem.jamTesSystem.response.ApiResponse;
import com.doziem.jamTesSystem.service.prescriptionService.IPrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrescriptionController.class)
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPrescriptionService prescriptionService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAllPrescriptionsReturnsList() throws Exception {
        when(prescriptionService.getAllPrescriptions()).thenReturn(List.of(
                PrescriptionDto.builder().id("rx-1").medicationName("Paracetamol").build()
        ));

        mockMvc.perform(get("/api/prescriptions/all").with(user("doctor").roles("DOCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void getPrescriptionByIdReturnsPrescription() throws Exception {
        when(prescriptionService.getPrescriptionById("rx-1"))
                .thenReturn(Optional.of(PrescriptionDto.builder().id("rx-1").medicationName("Paracetamol").build()));

        mockMvc.perform(get("/api/prescriptions/rx-1").with(user("doctor").roles("DOCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void createPrescriptionReturnsCreatedResponse() throws Exception {
        when(prescriptionService.prescribeMedication(any(PrescriptionDto.class), any()))
                .thenReturn(PrescriptionDto.builder().id("rx-1").medicationName("Paracetamol").build());

        mockMvc.perform(post("/api/prescriptions/create")
                        .with(user("doctor").roles("DOCTOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": "p-1",
                                  "prescribedBy": "d-1",
                                  "medicationName": "Paracetamol"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void updatePrescriptionReturnsUpdatedPrescription() throws Exception {
        when(prescriptionService.updatePrescription(eq("rx-1"), any(PrescriptionDto.class), any()))
                .thenReturn(PrescriptionDto.builder().id("rx-1").dosage("10mg").build());

        mockMvc.perform(put("/api/prescriptions/rx-1")
                        .with(user("doctor").roles("DOCTOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dosage\":\"10mg\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePrescriptionReturnsNoContent() throws Exception {
        doNothing().when(prescriptionService).deletePrescription("rx-1");

        mockMvc.perform(delete("/api/prescriptions/rx-1").with(user("doctor").roles("DOCTOR")).with(csrf()))
                .andExpect(status().isOk());
    }
}
