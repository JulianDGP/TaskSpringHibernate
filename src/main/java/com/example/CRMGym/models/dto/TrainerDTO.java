package com.example.CRMGym.models.dto;


import jakarta.validation.constraints.NotBlank;

public record TrainerDTO(Long id,
                         @NotBlank(message = "First Name is required.")
                         String firstName,
                         @NotBlank(message = "Last Name is required.")
                         String lastName,
                         String username,
                         boolean isActive,
                         @NotBlank(message = "Specialization is required.")
                         String specialization) {
}
