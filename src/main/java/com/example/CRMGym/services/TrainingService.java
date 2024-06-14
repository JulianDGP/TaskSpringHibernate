package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TrainingService {
    Set<Training> findTrainingsByTraineeId(Long traineeId);
//    Training createTraining(Training training);
//    Training getTraining(Long id);
//    List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType);
//    List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);
//
//    Training updateTraining(Long trainingId, Training updatedTraining);
//    void deleteTraining(Long trainingId);
//    List<Trainer> getUnassignedTrainers(String traineeUsername);
//    void updateTraineeTrainersList(String traineeUsername, List<Long> trainerIds);

}
