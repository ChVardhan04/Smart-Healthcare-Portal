package com.smarthealthcare.repository;

import com.smarthealthcare.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    Optional<Symptom> findByNameIgnoreCase(String name);
    List<Symptom> findByNameIgnoreCaseIn(List<String> names);
}
