package com.smarthealthcare.dto;

import com.smarthealthcare.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SlotResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Slot.Mode mode;
    private Integer capacity;
    private Integer remainingSeats;
    private boolean full;

    public static SlotResponse from(Slot s) {
        return new SlotResponse(s.getId(), s.getDate(), s.getStartTime(), s.getEndTime(),
                s.getMode(), s.getCapacity(), s.getRemainingSeats(), s.isFull());
    }
}
