package com.smarthealthcare.controller;

import com.smarthealthcare.dto.PredictionRequest;
import com.smarthealthcare.entity.Patient;
import com.smarthealthcare.entity.Symptom;
import com.smarthealthcare.service.CurrentUserService;
import com.smarthealthcare.service.DiseasePredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

    private final DiseasePredictionService predictionService;
    private final CurrentUserService currentUserService;

    @GetMapping("/symptoms")
    public List<Symptom> listSymptoms() {
        return predictionService.listAllSymptoms();
    }

    // Publicly accessible so a visitor can run a symptom check before creating an account.
    // If they're logged in as a patient, the result is linked to their history automatically.
    @PostMapping
    public List<DiseasePredictionService.DiseaseMatch> predict(@Valid @RequestBody PredictionRequest req) {
        Long patientId = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                Patient patient = currentUserService.getCurrentPatient();
                patientId = patient.getId();
            } catch (Exception ignored) {
                // caller authenticated but not a patient (e.g. a doctor testing it) - just don't link it
            }
        }
        return predictionService.predict(req.getSymptoms(), patientId);
    }
}
