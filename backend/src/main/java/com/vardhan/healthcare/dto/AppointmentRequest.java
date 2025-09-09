package com.vardhan.healthcare.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        @NotBlank String doctorName,
        @Future LocalDateTime appointmentTime
) {}
