package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TraineeProfileDTO;

import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);
    Trainee getTrainee(Long id);
    Trainee getTraineeByUsername(String username);
    TraineeProfileDTO getTraineeProfile(String username);
//    List<Trainer> getAssignedTrainers(String traineeUsername);
//    List<Trainee> getAllTrainees();
//    Trainee updateTrainee(Long id, Trainee trainee);
//    void deleteTraineeByUsername(String username);
//    void activateTrainee(Long id, boolean isActive);

//    void changeTraineePassword(Long id, String newPassword);
}
