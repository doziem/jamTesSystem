package com.doziem.jamTesSystem.dto;

import com.doziem.jamTesSystem.constant.Department;
import com.doziem.jamTesSystem.model.Pharmacy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyDto {

    private String id;
    private String name;
    private Department department;
    private boolean mainPharmacy;
    private String mainPharmacyId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isMainPharmacy() {
        return mainPharmacy;
    }

    public void setMainPharmacy(boolean mainPharmacy) {
        this.mainPharmacy = mainPharmacy;
    }

    public String getMainPharmacyId() {
        return mainPharmacyId;
    }

    public void setMainPharmacyId(String mainPharmacyId) {
        this.mainPharmacyId = mainPharmacyId;
    }
}
