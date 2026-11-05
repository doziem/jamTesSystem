package com.doziem.jamTesSystem.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePharmacyReorderRequest {
    @Positive
    private int quantity;
}
