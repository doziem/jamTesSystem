package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.model.Medication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationDto {

    private String id;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private boolean active;
    private int quantity;
}
