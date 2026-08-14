package com.smarthealthcare.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PredictionRequest {
    @NotEmpty(message = "Select at least one symptom")
    private List<String> symptoms;
}
