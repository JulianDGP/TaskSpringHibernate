package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;

import java.util.List;

public interface TraineeService {
    List<Trainee> getAllTrainees();
    Trainee getTrainee(Long id);
    Trainee getTraineeByUsername(String username);
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Long id, Trainee trainee);
    void deleteTraineeByUsername(String username);
    void activateTrainee(Long id, boolean isActive);

    void changeTraineePassword(Long id, String newPassword);
}
