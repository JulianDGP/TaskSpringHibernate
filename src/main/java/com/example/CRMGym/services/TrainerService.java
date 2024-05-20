package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrainerService {
    List<Trainer> getAllTrainers();
    Trainer getTrainer(Long id);
    Trainer getTrainerByUsername(String username);
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Long id, Trainer trainer);

    void changeTrainerPassword(Long id, String newPassword);
    void deleteTrainerByUsername(String username);
    void activateTrainer(Long id, boolean isActive);
}

