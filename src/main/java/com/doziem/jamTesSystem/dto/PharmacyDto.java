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

    public static Pharmacy mapToEntity(PharmacyDto dto, Pharmacy mainPharmacyRef) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setName(dto.getName());
        pharmacy.setDepartment(dto.getDepartment());
        pharmacy.setMainPharmacy(dto.isMainPharmacy());
        pharmacy.setMainPharmacyRef(mainPharmacyRef);
        return pharmacy;
    }

    public static PharmacyDto mapToDTO(Pharmacy pharmacy) {
        String parentId = pharmacy.getMainPharmacyRef() != null ? pharmacy.getMainPharmacyRef().getId() : null;
        return new PharmacyDto(
                pharmacy.getId(),
                pharmacy.getName(),
                pharmacy.getDepartment(),
                pharmacy.isMainPharmacy(),
                parentId
        );
    }

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
