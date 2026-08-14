package com.smarthealthcare.controller;

import com.smarthealthcare.dto.AppointmentStatusRequest;
import com.smarthealthcare.dto.SlotRequest;
import com.smarthealthcare.dto.SlotResponse;
import com.smarthealthcare.entity.Appointment;
import com.smarthealthcare.entity.Doctor;
import com.smarthealthcare.service.AppointmentService;
import com.smarthealthcare.service.CurrentUserService;
import com.smarthealthcare.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-dashboard")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final SlotService slotService;
    private final AppointmentService appointmentService;
    private final CurrentUserService currentUserService;

    @PostMapping("/slots")
    public SlotResponse createSlot(@Valid @RequestBody SlotRequest req) {
        Doctor doctor = currentUserService.getCurrentDoctor();
        return slotService.createSlot(doctor.getId(), req);
    }

    @GetMapping("/slots")
    public List<SlotResponse> mySlots(@RequestParam(required = false) LocalDate date) {
        Doctor doctor = currentUserService.getCurrentDoctor();
        return slotService.getAvailableSlots(doctor.getId(), date);
    }

    @DeleteMapping("/slots/{slotId}")
    public void deactivateSlot(@PathVariable Long slotId) {
        Doctor doctor = currentUserService.getCurrentDoctor();
        slotService.deactivateSlot(doctor.getId(), slotId);
    }

    @GetMapping("/appointments")
    public List<Appointment> myAppointments(@RequestParam(required = false) Appointment.Status status) {
        Doctor doctor = currentUserService.getCurrentDoctor();
        return appointmentService.getDoctorAppointments(doctor.getId(), status);
    }

    @PutMapping("/appointments/{id}/status")
    public Appointment decide(@PathVariable Long id, @Valid @RequestBody AppointmentStatusRequest req) {
        Doctor doctor = currentUserService.getCurrentDoctor();
        return appointmentService.decideAppointment(doctor.getId(), id, req);
    }
}
