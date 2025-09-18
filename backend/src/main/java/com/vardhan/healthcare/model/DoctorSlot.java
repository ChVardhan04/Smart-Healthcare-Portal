package com.vardhan.healthcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    private LocalDateTime slotTime;

    private boolean booked = false;
}
