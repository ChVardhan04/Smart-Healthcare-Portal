package com.smarthealthcare.dto;

import com.smarthealthcare.entity.Slot;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequest {
    @NotNull
    private Long doctorId;
    @NotNull
    private Long slotId;
    @NotNull
    private Slot.Mode mode;
    private String patientNotes;
    private Long predictionId; // optional link to a symptom-check
}
