package com.example.CRMGym.mappers;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;

import java.util.List;
import java.util.stream.Collectors;

public class TraineeMapper {

    //Converts a Trainee entity to a TraineeDTO.
    public static TraineeDTO toDTO(Trainee trainee) {
        if (trainee == null) {
            return null;
        }
        return new TraineeDTO(
                trainee.getId(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getUsername(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive()
        );
    }

    //Converts a TraineeDTO to a Trainee entity.
    public static Trainee toEntity(TraineeDTO traineeDTO) {
        if (traineeDTO == null) {
            return null;
        }
        Trainee trainee = new Trainee();
        trainee.setId(traineeDTO.id());
        trainee.setFirstName(traineeDTO.firstName());
        trainee.setLastName(traineeDTO.lastName());
        trainee.setUsername(traineeDTO.username());
        trainee.setDateOfBirth(traineeDTO.dateOfBirth());
        trainee.setAddress(traineeDTO.address());
        trainee.setActive(traineeDTO.isActive());
        return trainee;
    }

    // Converts a Trainee entity to a TraineeProfileDTO.
    public static TraineeProfileDTO toProfileDTO(Trainee trainee, List<TrainerDTO> trainerDTOs) {
        if (trainee == null) {
            return null;
        }
        return new TraineeProfileDTO(
                trainee.getId(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getUsername(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive(),
                trainerDTOs
        );
    }
}
