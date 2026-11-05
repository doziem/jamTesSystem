package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.ReorderRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PharmacyReorderRequestDto {
    private String id;
    private String pharmacyId;
    private String pharmacyName;
    private String medicationId;
    private String medicationName;
    private int quantity;
    private ReorderRequestStatus status;
    private String requestedByName;
    private String processedByName;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
