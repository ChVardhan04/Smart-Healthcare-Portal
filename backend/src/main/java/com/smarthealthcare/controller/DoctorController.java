package com.smarthealthcare.controller;

import com.smarthealthcare.dto.DoctorResponse;
import com.smarthealthcare.dto.SlotResponse;
import com.smarthealthcare.service.DoctorService;
import com.smarthealthcare.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final SlotService slotService;

    @GetMapping
    public List<DoctorResponse> search(@RequestParam(required = false) String specialty,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String location) {
        return doctorService.search(specialty, name, location);
    }

    @GetMapping("/{id}")
    public DoctorResponse getById(@PathVariable Long id) {
        return doctorService.getById(id);
    }

    // Public: patients browse a doctor's upcoming open slots (live remaining-seat count included)
    @GetMapping("/{id}/slots")
    public List<SlotResponse> getSlots(@PathVariable Long id,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return slotService.getAvailableSlots(id, date);
    }
}
