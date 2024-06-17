package com.example.CRMGym.mappers;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;

import java.util.List;
import java.util.stream.Collectors;

public class TrainerMapper {

    //Converts a Trainer entity to a TrainerDTO.
    public static TrainerDTO toDTO(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return new TrainerDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getUsername(),
                trainer.isActive(),
                trainer.getSpecialization()
        );
    }

    // Converts a TrainerDTO to a Trainer entity.
    public static Trainer toEntity(TrainerDTO trainerDTO) {
        if (trainerDTO == null) {
            return null;
        }
        Trainer trainer = new Trainer();
        trainer.setId(trainerDTO.id());
        trainer.setFirstName(trainerDTO.firstName());
        trainer.setLastName(trainerDTO.lastName());
        trainer.setUsername(trainerDTO.username());
        trainer.setActive(trainerDTO.isActive());
        trainer.setSpecialization(trainerDTO.specialization());
        return trainer;
    }

    //Converts a Trainer entity to a TrainerProfileDTO.
    public static TrainerProfileDTO toProfileDTO(Trainer trainer, List<TraineeDTO> trainees) {
        if (trainer == null) {
            return null;
        }
        return new TrainerProfileDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getUsername(),
                trainer.isActive(),
                trainer.getSpecialization(),
                trainees
        );
    }
}
