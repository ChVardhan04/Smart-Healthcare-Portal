package com.smarthealthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Slot.Mode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;

    @Column(length = 1000)
    private String patientNotes;

    @Column(length = 1000)
    private String doctorComment;

    // Optional link to a symptom-check that led to this booking
    private Long predictionId;

    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime decidedAt;

    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
    }

    public enum PaymentStatus {
        NOT_REQUIRED, PENDING_PAYMENT, PAID, FAILED, REFUNDED
    }
}
