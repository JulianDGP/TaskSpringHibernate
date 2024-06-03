package com.example.CRMGym.models.dto;

import java.time.LocalDateTime;

public record TrainingDTO(
        Long id,
        Long traineeId,
        Long trainerId,
        String trainingName,
        String trainingType,
        LocalDateTime trainingDate,
        int trainingDuration
) {
}
