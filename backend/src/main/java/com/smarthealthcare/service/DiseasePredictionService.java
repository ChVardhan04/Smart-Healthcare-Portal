package com.smarthealthcare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthealthcare.entity.*;
import com.smarthealthcare.exception.ApiException;
import com.smarthealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rule-based, explainable symptom-to-disease scorer.
 *
 * How it scores each disease:
 *  1. Sum the weights (1-10) of every submitted symptom that's linked to the disease.
 *  2. Normalize against the disease's maximum possible weight, so diseases with many
 *     mapped symptoms don't unfairly dominate diseases with fewer, more specific ones.
 *  3. Apply a penalty if a "core" (must-have) symptom for that disease is missing -
 *     e.g. you can't score high for a cardiac issue without chest pain/pressure present.
 *  4. Give a small bonus for match density (fraction of the patient's submitted
 *     symptoms that this disease actually explains), which rewards diseases that
 *     account for more of what the patient is reporting rather than partially fitting.
 *
 * This is intentionally NOT a diagnosis - it's a triage/information tool. Every
 * response is framed as "possible conditions to discuss with a doctor," not a
 * medical verdict, and severity is exposed to prompt urgent care when relevant.
 */
@Service
@RequiredArgsConstructor
public class DiseasePredictionService {

    private final SymptomRepository symptomRepository;
    private final DiseaseSymptomWeightRepository weightRepository;
    private final PredictionResultRepository predictionResultRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_RESULTS = 5;
    private static final double CORE_SYMPTOM_MISSING_PENALTY = 0.45; // multiply score by this if a core symptom absent
    private static final double MIN_SCORE_THRESHOLD = 8.0; // ignore near-zero noise matches

    public static class DiseaseMatch {
        public Long diseaseId;
        public String disease;
        public String specialty;
        public String severity;
        public double score; // 0-100
        public List<String> matchedSymptoms;
        public String description;

        public DiseaseMatch(Long diseaseId, String disease, String specialty, String severity,
                             double score, List<String> matchedSymptoms, String description) {
            this.diseaseId = diseaseId;
            this.disease = disease;
            this.specialty = specialty;
            this.severity = severity;
            this.score = score;
            this.matchedSymptoms = matchedSymptoms;
            this.description = description;
        }
    }

    @Transactional
    public List<DiseaseMatch> predict(List<String> rawSymptomNames, Long patientId) {
        if (rawSymptomNames == null || rawSymptomNames.isEmpty()) {
            throw new ApiException("Please select at least one symptom", HttpStatus.BAD_REQUEST);
        }

        List<String> normalized = rawSymptomNames.stream()
                .map(s -> s.trim().toLowerCase().replace(" ", "_"))
                .distinct()
                .collect(Collectors.toList());

        List<Symptom> symptoms = symptomRepository.findByNameIgnoreCaseIn(normalized);
        if (symptoms.isEmpty()) {
            throw new ApiException("None of the submitted symptoms were recognized", HttpStatus.BAD_REQUEST);
        }

        List<Long> symptomIds = symptoms.stream().map(Symptom::getId).collect(Collectors.toList());
        Map<Long, String> symptomIdToName = symptoms.stream()
                .collect(Collectors.toMap(Symptom::getId, s -> s.getDisplayName() != null ? s.getDisplayName() : s.getName()));

        List<DiseaseSymptomWeight> relevantWeights = weightRepository.findBySymptomIdIn(symptomIds);

        // Group by disease
        Map<Disease, List<DiseaseSymptomWeight>> byDisease = relevantWeights.stream()
                .collect(Collectors.groupingBy(DiseaseSymptomWeight::getDisease));

        List<DiseaseMatch> results = new ArrayList<>();

        for (Map.Entry<Disease, List<DiseaseSymptomWeight>> entry : byDisease.entrySet()) {
            Disease disease = entry.getKey();
            List<DiseaseSymptomWeight> matchedEdges = entry.getValue();

            int matchedWeightSum = matchedEdges.stream().mapToInt(DiseaseSymptomWeight::getWeight).sum();

            // Full symptom profile of this disease (matched + unmatched) to normalize against
            List<DiseaseSymptomWeight> allEdgesForDisease = weightRepository.findByDiseaseId(disease.getId());
            int totalPossibleWeight = allEdgesForDisease.stream().mapToInt(DiseaseSymptomWeight::getWeight).sum();

            double coverageScore = totalPossibleWeight > 0
                    ? (matchedWeightSum / (double) totalPossibleWeight) * 100.0
                    : 0.0;

            // Density bonus: what fraction of the PATIENT's submitted symptoms does this disease explain
            double densityBonus = (matchedEdges.size() / (double) symptoms.size()) * 15.0;

            double rawScore = Math.min(100.0, coverageScore + densityBonus);

            // Core-symptom penalty
            boolean missingCoreSymptom = allEdgesForDisease.stream()
                    .anyMatch(w -> w.isCoreSymptom() && matchedEdges.stream().noneMatch(m -> m.getId().equals(w.getId())));
            if (missingCoreSymptom) {
                rawScore *= CORE_SYMPTOM_MISSING_PENALTY;
            }

            if (rawScore < MIN_SCORE_THRESHOLD) continue;

            List<String> matchedNames = matchedEdges.stream()
                    .map(w -> symptomIdToName.get(w.getSymptom().getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            results.add(new DiseaseMatch(
                    disease.getId(), disease.getName(), disease.getRecommendedSpecialty(),
                    disease.getBaseSeverity().name(), Math.round(rawScore * 10.0) / 10.0,
                    matchedNames, disease.getDescription()
            ));
        }

        results.sort((a, b) -> Double.compare(b.score, a.score));
        List<DiseaseMatch> top = results.stream().limit(MAX_RESULTS).collect(Collectors.toList());

        if (top.isEmpty()) {
            // Nothing matched meaningfully - return a generic "see a doctor" fallback rather than an empty screen
            top.add(new DiseaseMatch(null, "Unspecified condition", "General Physician", "MODERATE",
                    0.0, List.of(), "Your symptoms didn't strongly match a specific condition in our database. A general physician can help narrow this down."));
        }

        persistResult(patientId, normalized, top);
        return top;
    }

    private void persistResult(Long patientId, List<String> normalizedSymptoms, List<DiseaseMatch> top) {
        try {
            PredictionResult result = new PredictionResult();
            if (patientId != null) {
                patientRepository.findById(patientId).ifPresent(result::setPatient);
            }
            result.setSymptomsInput(String.join(",", normalizedSymptoms));
            result.setResultJson(objectMapper.writeValueAsString(top));
            result.setTopDisease(top.get(0).disease);
            result.setRecommendedSpecialty(top.get(0).specialty);
            try {
                result.setTopSeverity(Disease.Severity.valueOf(top.get(0).severity));
            } catch (Exception ignored) {
                result.setTopSeverity(Disease.Severity.MODERATE);
            }
            predictionResultRepository.save(result);
        } catch (Exception ignored) {
            // Prediction persistence is best-effort; never fail the user-facing prediction because of it.
        }
    }

    @Transactional(readOnly = true)
    public List<Symptom> listAllSymptoms() {
        return symptomRepository.findAll();
    }
}
