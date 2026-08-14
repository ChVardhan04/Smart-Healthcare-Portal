package com.smarthealthcare.dto;

import com.smarthealthcare.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusRequest {
    @NotNull
    private Appointment.Status status; // APPROVED or REJECTED
    private String comment;
}
