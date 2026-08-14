package com.smarthealthcare.service;

import com.smarthealthcare.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails. Wrapped in try/catch so that a missing/misconfigured
 * SMTP server (very common in dev) never breaks the actual booking/approval flow -
 * it just logs and moves on. Runs @Async so email sending never blocks the request.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(User user) {
        send(user.getEmail(), "Welcome to Smart Healthcare",
                "Hi " + user.getName() + ",\n\nYour account has been created successfully as a " +
                        user.getRole() + ". You can now log in and get started.\n\n- Smart Healthcare Team");
    }

    @Async
    public void sendAppointmentRequestEmail(Doctor doctor, Appointment appt) {
        send(doctor.getUser().getEmail(), "New appointment request",
                "Hi Dr. " + doctor.getUser().getName() + ",\n\n" +
                        appt.getPatient().getUser().getName() + " has requested an appointment on " +
                        appt.getSlot().getDate() + " at " + appt.getSlot().getStartTime() +
                        " (" + appt.getMode() + ").\n\nPlease log in to approve or reject this request.");
    }

    @Async
    public void sendAppointmentStatusEmail(Appointment appt) {
        String subject = "Appointment " + appt.getStatus().name().toLowerCase();
        String body = "Hi " + appt.getPatient().getUser().getName() + ",\n\n" +
                "Your appointment with Dr. " + appt.getDoctor().getUser().getName() +
                " on " + appt.getSlot().getDate() + " at " + appt.getSlot().getStartTime() +
                " has been " + appt.getStatus().name().toLowerCase() + ".\n" +
                (appt.getDoctorComment() != null ? "Doctor's note: " + appt.getDoctorComment() + "\n" : "") +
                "\n- Smart Healthcare Team";
        send(appt.getPatient().getUser().getEmail(), subject, body);
    }

    @Async
    public void sendPaymentReceiptEmail(Appointment appt, Payment payment) {
        send(appt.getPatient().getUser().getEmail(), "Payment receipt",
                "Hi " + appt.getPatient().getUser().getName() + ",\n\nWe received your payment of ₹" +
                        payment.getAmount() + " for your appointment on " + appt.getSlot().getDate() +
                        ".\n\nThank you.");
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Email to {} could not be sent (SMTP not configured?): {}", to, e.getMessage());
        }
    }
}
