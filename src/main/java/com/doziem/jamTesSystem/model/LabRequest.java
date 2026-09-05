package com.doziem.jamTesSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lab_request")
public class LabRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false, columnDefinition = "uuid")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "requested_by", nullable = false, columnDefinition = "uuid")
    private User requestedBy;

    private LocalDate requestDate;

    @OneToMany(mappedBy = "labRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabReport> tests = new ArrayList<>();

    @PrePersist
    public void generateId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
