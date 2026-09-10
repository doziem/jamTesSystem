package com.doziem.jamTesSystem.controller.authController;

import com.doziem.jamTesSystem.constant.Role;
import com.doziem.jamTesSystem.request.AuthRequest;
import com.doziem.jamTesSystem.response.AuthResponse;
import com.doziem.jamTesSystem.service.authService.IAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void loginReturnsTokenForExistingUser() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmailOrPhone("jane@example.com");
        request.setPassword("Secret123");

        when(authService.login(any(AuthRequest.class))).thenReturn(
                new AuthResponse("Login successful", "Jane Doe", "jane@example.com", "08012345678", Role.PATIENT, true, "jwt-token")
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void registerCreatesUserAccount() throws Exception {
        String requestBody = "{\n" +
                "  \"name\": \"Jane Doe\",\n" +
                "  \"email\": \"jane@example.com\",\n" +
                "  \"phone\": \"08012345678\",\n" +
                "  \"password\": \"Secret123\",\n" +
                "  \"role\": \"PATIENT\",\n" +
                "  \"active\": true\n" +
                "}";

        when(authService.createUser(any())).thenReturn(
                new AuthResponse("User created successfully. Please check your email for verification.", "Jane Doe", "jane@example.com", "08012345678", Role.PATIENT, true, null)
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully. Please check your email for verification."));
    }

    @Test
    void verifyEmailReturnsOk() throws Exception {
        when(authService.verifyEmail("jane@example.com", "token")).thenReturn(
                new com.doziem.jamTesSystem.response.ApiResponse(true, "Email verified successfully", "jane@example.com")
        );

        mockMvc.perform(post("/auth/verify-email?email=jane@example.com&token=token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }
}
