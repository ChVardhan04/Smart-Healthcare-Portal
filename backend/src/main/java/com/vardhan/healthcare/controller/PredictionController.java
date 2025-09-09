package com.vardhan.healthcare.controller;

import com.vardhan.healthcare.dto.PredictionRequest;
import com.vardhan.healthcare.dto.PredictionResponse;
import com.vardhan.healthcare.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping("/diabetes")
    public PredictionResponse predictDiabetes(@RequestBody PredictionRequest request) {
        return predictionService.predictDiabetes(request);
    }
}
