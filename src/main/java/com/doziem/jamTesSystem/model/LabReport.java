package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LabReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private String result;

    private String reportDate;

    private String conductedBy;

    public LabReport(){}

    public LabReport(UUID id, String result, String testName, Patient patient,
                     String reportDate, String conductedBy) {
        this.id = id;
        this.result = result;
        this.testName = testName;
        this.patient = patient;
        this.reportDate = reportDate;
        this.conductedBy = conductedBy;
    }

    public LabReport(UUID id, UUID patientId, String testName, String result,
                     String reportDate, String conductedBy) {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getConductedBy() {
        return conductedBy;
    }

    public void setConductedBy(String conductedBy) {
        this.conductedBy = conductedBy;
    }
}
