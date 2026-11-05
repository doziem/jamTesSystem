package com.doziem.jamTesSystem.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LabReportDto {
    private String id;
    private String patientId;
    private String requestedBy;
    private LocalDate reportDate;
    private LocalDate requestDate;
    private String conductedBy;
    private String labRequestId;
    private List<String> testNames = new ArrayList<>();
    private List<LabTestResultDto> testResults = new ArrayList<>();
}