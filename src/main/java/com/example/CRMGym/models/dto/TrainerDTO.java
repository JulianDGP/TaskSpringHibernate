package com.example.CRMGym.models.dto;

import com.example.CRMGym.models.Trainee;

/*Converts a Trainee entity to a TraineeDTO.*/
public record TrainerDTO(Long id,
                         String firstName,
                         String lastName,
                         String username,
                         boolean isActive,
                         String specialization) {
}
