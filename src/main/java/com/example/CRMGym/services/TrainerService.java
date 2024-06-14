package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrainerService {
    Trainer createTrainer(Trainer trainer);

    Trainer getTrainer(Long id);
    Trainer getTrainerByUsername(String username);
//    Trainer updateTrainer(Long id, Trainer trainer);
//    List<Trainer> getAllTrainers();
//
//    void changeTrainerPassword(Long id, String newPassword);
//    void deleteTrainerByUsername(String username);
//    void activateTrainer(Long id, boolean isActive);
}

