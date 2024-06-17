package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee getTrainee(Long id);
    Trainee getTraineeByUsername(String username);
    TraineeProfileDTO getTraineeProfile(String username);
    TraineeProfileDTO updateTraineeProfile(String username, TraineeDTO traineeDTO);
    void deleteTraineeByUsername(String username);
    List<TrainerDTO> getNotAssignedActiveTrainers(String username);

    List<TrainingDTO> getTraineeTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);

    void updateTraineeActiveStatus(String username, boolean isActive);

//    List<Trainee> getAllTrainees();

}
