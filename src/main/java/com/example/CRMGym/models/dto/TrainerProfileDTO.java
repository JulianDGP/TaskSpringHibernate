package com.example.CRMGym.models.dto;

import java.util.List;

public record TrainerProfileDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        boolean isActive,
        String specialization,
        List<TraineeDTO> trainees
) {
}
