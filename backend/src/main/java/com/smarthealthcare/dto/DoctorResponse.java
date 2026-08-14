package com.smarthealthcare.dto;

import com.smarthealthcare.entity.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String specialty;
    private String qualifications;
    private String clinicAddress;
    private String location;
    private Double consultationFee;
    private Double onlineFee;
    private String bio;
    private Integer yearsExperience;

    public static DoctorResponse from(Doctor d) {
        return new DoctorResponse(d.getId(), d.getUser().getName(), d.getSpecialty(),
                d.getQualifications(), d.getClinicAddress(), d.getLocation(),
                d.getConsultationFee(), d.getOnlineFee(), d.getBio(), d.getYearsExperience());
    }
}
