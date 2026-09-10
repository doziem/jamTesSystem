package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "encounters")
public class Encounter {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String visitType = "OPD";

    @Column(nullable = false)
    private String status = "ARRIVED";

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    private String departmentName;

    private String assignedDoctorId;

    @Column(length = 1000)
    private String triageNotes;

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.arrivalTime == null) {
            this.arrivalTime = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "ARRIVED";
        }
        if (this.visitType == null || this.visitType.isBlank()) {
            this.visitType = "OPD";
        }
    }
}
