package com.smarthealthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mode mode;

    @Column(nullable = false)
    private Integer capacity;

    // Tracks live remaining availability. Decremented on each confirmed booking,
    // incremented back on cancellation/rejection. This is what drives the
    // "slot filled -> updates accordingly" behaviour on the frontend.
    @Column(nullable = false)
    private Integer remainingSeats;

    @Column(nullable = false)
    private boolean active = true;

    // Optimistic locking: prevents two simultaneous bookings from both reading
    // remainingSeats=1 and both succeeding (classic overbooking race condition).
    @Version
    private Long version;

    public enum Mode {
        ONLINE, OFFLINE
    }

    @Transient
    public boolean isFull() {
        return remainingSeats <= 0;
    }
}
