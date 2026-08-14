package com.smarthealthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String recommendedSpecialty; // maps to Doctor.specialty

    @Enumerated(EnumType.STRING)
    private Severity baseSeverity = Severity.MODERATE;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiseaseSymptomWeight> symptomWeights = new HashSet<>();

    public enum Severity {
        LOW, MODERATE, HIGH, EMERGENCY
    }
}
