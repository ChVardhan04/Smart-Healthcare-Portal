package com.smarthealthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient; // nullable: allow anonymous symptom checks too

    @Column(nullable = false, length = 1000)
    private String symptomsInput; // comma-separated symptom names submitted

    @Column(nullable = false, length = 4000)
    private String resultJson; // serialized ranked list of {disease, score, specialty}

    @Column(nullable = false)
    private String topDisease;

    private String recommendedSpecialty;

    @Enumerated(EnumType.STRING)
    private Disease.Severity topSeverity;

    private LocalDateTime createdAt = LocalDateTime.now();
}
