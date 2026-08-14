package com.smarthealthcare.controller;

import com.smarthealthcare.dto.AppointmentRequest;
import com.smarthealthcare.entity.Appointment;
import com.smarthealthcare.entity.Patient;
import com.smarthealthcare.service.AppointmentService;
import com.smarthealthcare.service.CurrentUserService;
import com.smarthealthcare.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    @PostMapping("/me/appointments")
    public Appointment book(@Valid @RequestBody AppointmentRequest req) {
        Patient patient = currentUserService.getCurrentPatient();
        return appointmentService.bookAppointment(patient.getId(), req);
    }

    @GetMapping("/me/appointments")
    public List<Appointment> myAppointments() {
        Patient patient = currentUserService.getCurrentPatient();
        return appointmentService.getPatientAppointments(patient.getId());
    }

    @PostMapping("/me/appointments/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) {
        Patient patient = currentUserService.getCurrentPatient();
        return appointmentService.cancelAppointment(patient.getId(), id);
    }

    @PostMapping("/me/appointments/{id}/pay/create-intent")
    public Object createPaymentIntent(@PathVariable Long id) {
        Patient patient = currentUserService.getCurrentPatient();
        return paymentService.createIntent(id, patient.getId());
    }

    @PostMapping("/me/payments/{paymentId}/confirm")
    public Object confirmPayment(@PathVariable Long paymentId) {
        Patient patient = currentUserService.getCurrentPatient();
        return paymentService.confirmPayment(paymentId, patient.getId());
    }
}
