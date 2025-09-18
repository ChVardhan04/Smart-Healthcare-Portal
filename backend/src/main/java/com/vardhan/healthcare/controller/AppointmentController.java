package com.vardhan.healthcare.controller;

import com.vardhan.healthcare.model.Appointment;
import com.vardhan.healthcare.model.DoctorSlot;
import com.vardhan.healthcare.service.AppointmentService;
import com.vardhan.healthcare.repo.DoctorSlotRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorSlotRepo doctorSlotRepo;

    @PostMapping("/book")
    public Appointment book(@RequestBody Appointment request) {
        return appointmentService.bookAppointment(request);
    }

    @PutMapping("/{id}/approve")
    public Appointment approve(@PathVariable Long id) {
        return appointmentService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public Appointment reject(@PathVariable Long id) {
        return appointmentService.reject(id);
    }

    @PutMapping("/{id}/confirm")
    public Appointment confirm(@PathVariable Long id) {
        return appointmentService.confirmAfterPayment(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> patientAppointments(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> doctorAppointments(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsForDoctor(doctorId);
    }

    @GetMapping("/slots/{doctorId}")
    public List<DoctorSlot> availableSlots(@PathVariable Long doctorId) {
        return doctorSlotRepo.findByDoctorIdAndBookedFalse(doctorId);
    }
}
