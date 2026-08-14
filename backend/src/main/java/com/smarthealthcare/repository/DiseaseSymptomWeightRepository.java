package com.smarthealthcare.repository;

import com.smarthealthcare.entity.DiseaseSymptomWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiseaseSymptomWeightRepository extends JpaRepository<DiseaseSymptomWeight, Long> {
    List<DiseaseSymptomWeight> findBySymptomIdIn(List<Long> symptomIds);
    List<DiseaseSymptomWeight> findByDiseaseId(Long diseaseId);
}
