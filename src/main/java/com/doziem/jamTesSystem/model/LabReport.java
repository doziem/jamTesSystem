package com.doziem.jamTesSystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class LabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    private UUID requestedBy;

    @Column(nullable = false)
    private String testName;

    private String result;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestDate;

    private UUID conductedBy;

    public LabReport() {}

    public LabReport(UUID id, Patient patient, UUID requestedBy, String testName, String result,
                     LocalDate reportDate, LocalDate requestDate, UUID conductedBy) {
        this.id = id;
        this.patient = patient;
        this.requestedBy = requestedBy;
        this.testName = testName;
        this.result = result;
        this.reportDate = reportDate;
        this.requestDate = requestDate;
        this.conductedBy = conductedBy;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public UUID getConductedBy() { return conductedBy; }
    public void setConductedBy(UUID conductedBy) { this.conductedBy = conductedBy; }
}
