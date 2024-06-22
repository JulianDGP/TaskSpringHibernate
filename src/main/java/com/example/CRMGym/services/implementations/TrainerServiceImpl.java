package com.example.CRMGym.services.implementations;

import com.example.CRMGym.mappers.TraineeMapper;
import com.example.CRMGym.mappers.TrainerMapper;
import com.example.CRMGym.mappers.TrainingMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;

    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional
    public Trainer createTrainer(Trainer trainer) {
        log.debug("Creating Trainer: {}", trainer);
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer created successfully with ID: {}", savedTrainer.getId());
        return savedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainer(Long id) {
        log.debug("Retrieving Trainer by ID: {}", id);
        return trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainerByUsername(String username) {
        log.debug("Retrieving Trainer by username: {}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
    }


    @Override
    @Transactional(readOnly = true)
    public TrainerProfileDTO getTrainerProfile(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        Set<Training> trainings = trainingRepository.findByTrainerId(trainer.getId());
        Set<Trainee> trainees = trainings.stream()
                .map(Training::getTrainee)
                .collect(Collectors.toSet());

        List<TraineeDTO> traineeDTOs = trainees.stream()
                .map(TraineeMapper::toDTO)
                .collect(Collectors.toList());

        return TrainerMapper.toProfileDTO(trainer, traineeDTOs);
    }

    @Override
    @Transactional
    public TrainerProfileDTO updateTrainerProfile(String username, TrainerDTO trainerDTO) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        // Ensure the username in DTO matches the username in the path
        if (!trainerDTO.username().equals(username)) {
            throw new IllegalArgumentException("Username cannot be changed.");
        }

        trainer.setFirstName(trainerDTO.firstName());
        trainer.setLastName(trainerDTO.lastName());
        trainer.setActive(trainerDTO.isActive());

        Trainer updatedTrainer = trainerRepository.save(trainer);

        Set<Training> trainings = trainingRepository.findByTrainerId(trainer.getId());
        Set<Trainee> trainees = trainings.stream()
                .map(Training::getTrainee)
                .collect(Collectors.toSet());

        List<TraineeDTO> traineeDTOs = trainees.stream()
                .map(TraineeMapper::toDTO)
                .collect(Collectors.toList());

        return TrainerMapper.toProfileDTO(updatedTrainer, traineeDTOs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO> getTrainerTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        List<Training> trainings = trainingRepository.findTrainingsByTrainerFilters(username, fromDate, toDate, traineeName);
        return trainings.stream()
                .map(TrainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTrainerActiveStatus(String username, boolean isActive) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        trainer.setActive(isActive);
        trainerRepository.save(trainer);
        log.info("Updated trainer with username: {} to active status: {}", username, isActive);
    }

}