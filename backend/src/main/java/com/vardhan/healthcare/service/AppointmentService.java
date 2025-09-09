package com.vardhan.healthcare.service;

import com.vardhan.healthcare.dto.AppointmentRequest;
import com.vardhan.healthcare.model.Appointment;
import com.vardhan.healthcare.model.User;
import com.vardhan.healthcare.repo.AppointmentRepo;
import com.vardhan.healthcare.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final UserRepo userRepo;

    public Appointment book(AppointmentRequest request) {
        User patient = userRepo.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctorName(request.doctorName())
                .appointmentTime(request.appointmentTime())
                .status("BOOKED")
                .build();

        return appointmentRepo.save(appointment);
    }
}
