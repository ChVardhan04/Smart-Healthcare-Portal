package com.smarthealthcare.dto;

import com.smarthealthcare.entity.Slot;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private Slot.Mode mode;
    @NotNull
    private Integer capacity;
}
