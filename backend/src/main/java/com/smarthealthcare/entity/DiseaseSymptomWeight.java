package com.smarthealthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Weighted edge between a Disease and a Symptom.
 * weight: how strongly this symptom indicates the disease (1-10).
 * isCoreSymptom: if true, absence of this symptom significantly lowers
 * the match score (used for "must-have" symptoms like chest pain for cardiac issues).
 */
@Entity
@Table(name = "disease_symptom_weights",
       uniqueConstraints = @UniqueConstraint(columnNames = {"disease_id", "symptom_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseSymptomWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @ManyToOne
    @JoinColumn(name = "symptom_id", nullable = false)
    private Symptom symptom;

    @Column(nullable = false)
    private Integer weight; // 1-10

    private boolean coreSymptom = false;
}
