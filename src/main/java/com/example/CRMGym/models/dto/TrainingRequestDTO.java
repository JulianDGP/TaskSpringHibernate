package com.example.CRMGym.models.dto;

import java.time.LocalDateTime;

public record TrainingRequestDTO(
        String traineeUsername,
        String trainerUsername,
        String trainingName,
        LocalDateTime trainingDate,
        int trainingDuration
) {
}
