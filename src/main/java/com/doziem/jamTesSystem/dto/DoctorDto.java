package com.doziem.jamTesSystem.dto;

import lombok.*;

@Builder
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    private String id;
    private String fullName;
    private String specialization;
    private int experience;
    private String userId;
    private String email;
    private String phoneNumber;
    private String availability;
}
