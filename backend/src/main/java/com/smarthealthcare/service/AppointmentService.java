package com.smarthealthcare.service;

import com.smarthealthcare.dto.AppointmentRequest;
import com.smarthealthcare.dto.AppointmentStatusRequest;
import com.smarthealthcare.entity.*;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SlotService slotService;
    private final EmailService emailService;

    @Transactional
    public Appointment bookAppointment(Long patientId, AppointmentRequest req) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ApiException("Patient not found", HttpStatus.NOT_FOUND));
        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new ApiException("Doctor not found", HttpStatus.NOT_FOUND));

        // reserveSeat locks the row and atomically decrements remainingSeats,
        // throwing ApiException(409) if the slot filled up in the meantime.
        Slot slot = slotService.reserveSeat(req.getSlotId());

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            slotService.releaseSeat(slot.getId());
            throw new ApiException("Slot does not belong to the selected doctor", HttpStatus.BAD_REQUEST);
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setSlot(slot);
        appt.setMode(req.getMode());
        appt.setStatus(Appointment.Status.PENDING);
        appt.setPatientNotes(req.getPatientNotes());
        appt.setPredictionId(req.getPredictionId());
        appt.setPaymentStatus(req.getMode() == Slot.Mode.ONLINE
                ? Appointment.PaymentStatus.PENDING_PAYMENT
                : Appointment.PaymentStatus.NOT_REQUIRED);

        appt = appointmentRepository.save(appt);
        emailService.sendAppointmentRequestEmail(doctor, appt);
        return appt;
    }

    @Transactional
    public Appointment decideAppointment(Long doctorId, Long appointmentId, AppointmentStatusRequest req) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException("Appointment not found", HttpStatus.NOT_FOUND));

        if (!appt.getDoctor().getId().equals(doctorId)) {
            throw new ApiException("Not authorized to act on this appointment", HttpStatus.FORBIDDEN);
        }
        if (appt.getStatus() != Appointment.Status.PENDING) {
            throw new ApiException("This appointment has already been " + appt.getStatus().name().toLowerCase(), HttpStatus.CONFLICT);
        }
        if (req.getStatus() != Appointment.Status.APPROVED && req.getStatus() != Appointment.Status.REJECTED) {
            throw new ApiException("Status must be APPROVED or REJECTED", HttpStatus.BAD_REQUEST);
        }

        appt.setStatus(req.getStatus());
        appt.setDoctorComment(req.getComment());
        appt.setDecidedAt(LocalDateTime.now());

        // Rejection frees the seat back up so another patient can take it -
        // this is the other half of "slot updates accordingly".
        if (req.getStatus() == Appointment.Status.REJECTED) {
            slotService.releaseSeat(appt.getSlot().getId());
        }

        appt = appointmentRepository.save(appt);
        emailService.sendAppointmentStatusEmail(appt);
        return appt;
    }

    @Transactional
    public Appointment cancelAppointment(Long patientId, Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException("Appointment not found", HttpStatus.NOT_FOUND));

        if (!appt.getPatient().getId().equals(patientId)) {
            throw new ApiException("Not authorized to cancel this appointment", HttpStatus.FORBIDDEN);
        }
        if (appt.getStatus() == Appointment.Status.CANCELLED || appt.getStatus() == Appointment.Status.COMPLETED) {
            throw new ApiException("This appointment cannot be cancelled", HttpStatus.CONFLICT);
        }

        boolean wasHoldingASeat = appt.getStatus() == Appointment.Status.PENDING || appt.getStatus() == Appointment.Status.APPROVED;
        appt.setStatus(Appointment.Status.CANCELLED);
        appt = appointmentRepository.save(appt);

        if (wasHoldingASeat) {
            slotService.releaseSeat(appt.getSlot().getId());
        }
        emailService.sendAppointmentStatusEmail(appt);
        return appt;
    }

    @Transactional(readOnly = true)
    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByRequestedAtDesc(patientId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getDoctorAppointments(Long doctorId, Appointment.Status status) {
        return status != null
                ? appointmentRepository.findByDoctorIdAndStatusOrderByRequestedAtDesc(doctorId, status)
                : appointmentRepository.findByDoctorIdOrderByRequestedAtDesc(doctorId);
    }
}
