package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainerService {
    Trainer createTrainer(Trainer trainer);

    Trainer getTrainer(Long id);
    TrainerProfileDTO getTrainerProfile(String username);
    Trainer getTrainerByUsername(String username);
    TrainerProfileDTO updateTrainerProfile(String username, TrainerDTO trainerDTO);
    List<TrainingDTO> getTrainerTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String traineeName);

    void updateTrainerActiveStatus(String username, boolean isActive);

}

