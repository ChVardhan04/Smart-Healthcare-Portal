package com.vardhan.healthcare.service;

import com.vardhan.healthcare.model.Appointment;
import com.vardhan.healthcare.model.DoctorSlot;
import com.vardhan.healthcare.repo.AppointmentRepo;
import com.vardhan.healthcare.repo.DoctorSlotRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final DoctorSlotRepo doctorSlotRepo;

    public Appointment bookAppointment(Appointment request) {
        DoctorSlot slot = doctorSlotRepo.findById(request.getSlot().getId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) throw new RuntimeException("Slot already booked");

        slot.setBooked(true);
        doctorSlotRepo.save(slot);

        request.setStatus("PENDING");
        return appointmentRepo.save(request);
    }

    public Appointment approve(Long id) {
        Appointment appt = appointmentRepo.findById(id).orElseThrow();
        appt.setStatus("APPROVED");
        return appointmentRepo.save(appt);
    }

    public Appointment reject(Long id) {
        Appointment appt = appointmentRepo.findById(id).orElseThrow();
        appt.setStatus("REJECTED");
        return appointmentRepo.save(appt);
    }

    public Appointment confirmAfterPayment(Long id) {
        Appointment appt = appointmentRepo.findById(id).orElseThrow();
        appt.setStatus("CONFIRMED");
        return appointmentRepo.save(appt);
    }

    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepo.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepo.findByDoctorId(doctorId);
    }
}
