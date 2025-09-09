package com.vardhan.healthcare.service;

import com.vardhan.healthcare.dto.PredictionRequest;
import com.vardhan.healthcare.dto.PredictionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final WebClient webClient;

    public PredictionResponse predictDiabetes(PredictionRequest request) {
        return webClient.post()
                .uri("/predict/diabetes")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .block();
    }
}
