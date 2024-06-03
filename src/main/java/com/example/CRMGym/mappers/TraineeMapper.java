package com.example.CRMGym.mappers;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;

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
                trainee.getAddress()
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
        return trainee;
    }
}
