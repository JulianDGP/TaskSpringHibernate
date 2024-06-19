package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TrainingRequestDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TrainingService {
    public Training createTraining(TrainingRequestDTO trainingRequestDTO);
    Training getTraining(Long id);

}
