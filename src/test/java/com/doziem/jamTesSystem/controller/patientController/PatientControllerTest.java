package com.doziem.jamTesSystem.controller.patientController;

import com.doziem.jamTesSystem.dto.PatientDto;
import com.doziem.jamTesSystem.service.patientService.IPatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private IPatientService patientService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PatientController(patientService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createPatientReturnsOk() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane@example.com");
        dto.setPhone("08012345678");
        dto.setGender("F");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setActive(true);

        when(patientService.createPatient(any(PatientDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@example.com\",\"phone\":\"08012345678\",\"gender\":\"F\",\"dateOfBirth\":\"2000-01-01\",\"active\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void getPatientByIdReturnsOk() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId("p-1");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        when(patientService.getPatientById("p-1")).thenReturn(dto);

        mockMvc.perform(get("/api/patients/p-1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPatientsReturnsOk() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId("p-1");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        when(patientService.getAllPatients(0, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/patients/all"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePatientReturnsOk() throws Exception {
        PatientDto dto = new PatientDto();
        dto.setId("p-1");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");

        when(patientService.updatePatient(eq("p-1"), any(PatientDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/patients/p-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"p-1\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"dateOfBirth\":\"1991-02-02\",\"active\":false}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePatientReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/patients/p-1"))
                .andExpect(status().isNoContent());
    }
}
