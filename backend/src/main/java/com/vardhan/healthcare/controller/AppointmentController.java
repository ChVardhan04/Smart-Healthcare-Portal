package com.vardhan.healthcare.controller;

import com.vardhan.healthcare.dto.AppointmentRequest;
import com.vardhan.healthcare.model.Appointment;
import com.vardhan.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public Appointment book(@RequestBody @Valid AppointmentRequest request) {
        return appointmentService.book(request);
    }
}
