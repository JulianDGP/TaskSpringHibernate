package com.example.CRMGym.services;

import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TrainingRequestDTO;

public interface TrainingService {
    Training createTraining(TrainingRequestDTO trainingRequestDTO);
    Training getTraining(Long id);

}
