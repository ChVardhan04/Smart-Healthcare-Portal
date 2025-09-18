package com.vardhan.healthcare.dto;

import java.util.List;

public record PredictionRequest(List<Double> features) {}
