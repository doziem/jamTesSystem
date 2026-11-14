package com.doziem.jamTesSystem.dto;

import jakarta.validation.constraints.NotBlank;

public record CloudinaryProperties(
        @NotBlank String cloudName,
        @NotBlank String apiKey,
        @NotBlank String apiSecret
) { }
