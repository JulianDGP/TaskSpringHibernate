package com.example.CRMGym.models.dto;


public record TrainerDTO(Long id,
                         String firstName,
                         String lastName,
                         String username,
                         boolean isActive,
                         String specialization) {
}
