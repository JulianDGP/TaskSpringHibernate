package com.example.CRMGym.mappers;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.services.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Autowired
    public TrainingMapper(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    //Converts a Training entity to a TrainingDTO.
    public static TrainingDTO toDTO(Training training) {
        if (training == null) {
            return null;
        }
        return new TrainingDTO(
                training.getId(),
                training.getTrainee() != null ? training.getTrainee().getId() : null,
                training.getTrainer() != null ? training.getTrainer().getId() : null,
                training.getTrainingName(),
                training.getTrainingType().name(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }

    // Converts a TrainingDTO to a Training entity.
    public Training toEntity(TrainingDTO trainingDTO) {
        if (trainingDTO == null) {
            return null;
        }
        Training training = new Training();
        training.setId(trainingDTO.id());
        training.setTrainee(traineeService.getTrainee(trainingDTO.traineeId()));
        training.setTrainer(trainerService.getTrainer(trainingDTO.trainerId()));
        training.setTrainingName(trainingDTO.trainingName());
        training.setTrainingType(TrainingType.valueOf(trainingDTO.trainingType()));
        training.setTrainingDate(trainingDTO.trainingDate());
        training.setTrainingDuration(trainingDTO.trainingDuration());

        return training;
    }

    // Converts a TrainingRequestDTO to a Training entity.
    public Training toEntity(TrainingRequestDTO trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            return null;
        }
        Trainee trainee = traineeService.getTraineeByUsername(trainingRequestDTO.traineeUsername());
        Trainer trainer = trainerService.getTrainerByUsername(trainingRequestDTO.trainerUsername());

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingRequestDTO.trainingName());
        training.setTrainingDate(trainingRequestDTO.trainingDate());
        training.setTrainingDuration(trainingRequestDTO.trainingDuration());

        return training;
    }
}
