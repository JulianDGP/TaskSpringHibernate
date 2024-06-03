package com.example.CRMGym.models.dto;

import java.time.LocalDate;

public record TraineeDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        String address
) {
}
