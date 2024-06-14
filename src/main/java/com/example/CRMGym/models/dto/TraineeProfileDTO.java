package com.example.CRMGym.models.dto;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        String address,
        boolean isActive,
        List<TrainerDTO> trainers
) {
}
