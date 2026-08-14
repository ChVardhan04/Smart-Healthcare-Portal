package com.smarthealthcare.repository;

import com.smarthealthcare.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {
    List<PredictionResult> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
