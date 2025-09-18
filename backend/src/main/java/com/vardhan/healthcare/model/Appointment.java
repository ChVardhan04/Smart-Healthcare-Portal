package com.vardhan.healthcare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    private DoctorSlot slot;

    private String mode;   // ONLINE / OFFLINE
    private String reason; // optional

    private String status; // PENDING, APPROVED, REJECTED, CONFIRMED
}
