package com.smarthealthcare.dto;

import com.smarthealthcare.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;

    @NotNull
    private User.Role role; // PATIENT or DOCTOR

    // Doctor-only optional fields
    private String specialty;
    private String qualifications;
    private String clinicAddress;
    private String location;
    private Double consultationFee;
    private Double onlineFee;
    private Integer yearsExperience;
}
