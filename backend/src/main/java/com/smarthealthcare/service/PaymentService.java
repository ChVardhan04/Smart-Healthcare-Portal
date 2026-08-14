package com.smarthealthcare.service;

import com.smarthealthcare.entity.Appointment;
import com.smarthealthcare.entity.Payment;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.AppointmentRepository;
import com.smarthealthcare.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mock payment provider integration. Mirrors the shape a real Stripe/Razorpay
 * integration would take (create-intent -> client completes payment -> confirm)
 * so swapping in a real provider later only touches this class.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    @Transactional
    public Payment createIntent(Long appointmentId, Long patientId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException("Appointment not found", HttpStatus.NOT_FOUND));
        if (!appt.getPatient().getId().equals(patientId)) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }
        if (appt.getPaymentStatus() != Appointment.PaymentStatus.PENDING_PAYMENT) {
            throw new ApiException("This appointment does not require payment right now", HttpStatus.BAD_REQUEST);
        }

        double amount = appt.getDoctor().getOnlineFee() != null ? appt.getDoctor().getOnlineFee() : 500.0;

        Payment payment = new Payment();
        payment.setAppointment(appt);
        payment.setAmount(amount);
        payment.setProvider("MOCK");
        payment.setProviderPaymentId("pi_" + UUID.randomUUID().toString().substring(0, 12));
        payment.setStatus(Payment.Status.CREATED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment confirmPayment(Long paymentId, Long patientId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));
        Appointment appt = payment.getAppointment();
        if (!appt.getPatient().getId().equals(patientId)) {
            throw new ApiException("Not authorized", HttpStatus.FORBIDDEN);
        }

        payment.setStatus(Payment.Status.SUCCEEDED);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        appt.setPaymentStatus(Appointment.PaymentStatus.PAID);
        appointmentRepository.save(appt);

        emailService.sendPaymentReceiptEmail(appt, payment);
        return payment;
    }
}
