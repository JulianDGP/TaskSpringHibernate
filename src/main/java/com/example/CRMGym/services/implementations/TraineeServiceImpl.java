package com.example.CRMGym.services.implementations;

import com.example.CRMGym.mappers.TraineeMapper;
import com.example.CRMGym.mappers.TrainerMapper;
import com.example.CRMGym.mappers.TrainingMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.TraineeService;

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
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private final TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository, TrainingRepository trainingRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    /* 1.Trainee Registration */
    @Override
    @Transactional
    public Trainee createTrainee(Trainee trainee) {
        log.debug("Saving Trainee: {}", trainee);
        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Trainee created successfully with ID: {}", savedTrainee.getId());
        return savedTrainee;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTrainee(Long id) {
        log.debug("Retrieving Trainee with ID: {}", id);
        return traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        log.debug("Retrieving Trainee by username: {}", username);
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));
    }
    @Override
    @Transactional(readOnly = true)
    public TraineeProfileDTO getTraineeProfile(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        Set<Training> trainings = trainingRepository.findByTraineeId(trainee.getId());
        Set<Trainer> trainers = trainings.stream()
                .map(Training::getTrainer)
                .collect(Collectors.toSet());
        List<TrainerDTO> trainerDTOs = trainers.stream()
                .map(TrainerMapper::toDTO)
                .collect(Collectors.toList());

        return TraineeMapper.toProfileDTO(trainee, trainerDTOs);
    }

    @Override
    @Transactional
    public TraineeProfileDTO updateTraineeProfile(String username, TraineeDTO traineeDTO) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        // Ensure the username in DTO matches the username in the path
        if (!traineeDTO.username().equals(username)) {
            throw new IllegalArgumentException("Username cannot be changed.");
        }
        trainee.setFirstName(traineeDTO.firstName());
        trainee.setLastName(traineeDTO.lastName());
        trainee.setDateOfBirth(traineeDTO.dateOfBirth());
        trainee.setAddress(traineeDTO.address());
        trainee.setActive(traineeDTO.isActive());

        Trainee updatedTrainee = traineeRepository.save(trainee);

        Set<Training> trainings = trainingRepository.findByTraineeId(trainee.getId());
        Set<Trainer> trainers = trainings.stream()
                .map(Training::getTrainer)
                .collect(Collectors.toSet());

        List<TrainerDTO> trainerDTOs = trainers.stream()
                .map(TrainerMapper::toDTO)
                .collect(Collectors.toList());

        return TraineeMapper.toProfileDTO(updatedTrainee, trainerDTOs);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        // Ensures that related workouts are cascaded away
        traineeRepository.delete(trainee);
        log.info("Deleted trainee with username: {}", username);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TrainerDTO> getNotAssignedActiveTrainers(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        List<Trainer> activeTrainers = trainerRepository.findAllByIsActive(true);
        List<Trainer> assignedTrainers = trainerRepository.findTrainersByTraineeId(trainee.getId());

        Set<Long> assignedTrainerIds = assignedTrainers.stream()
                .map(Trainer::getId)
                .collect(Collectors.toSet());

        List<Trainer> notAssignedTrainers = activeTrainers.stream()
                .filter(trainer -> !assignedTrainerIds.contains(trainer.getId()))
                .toList();

        return notAssignedTrainers.stream()
                .map(TrainerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO> getTraineeTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        List<Training> trainings = trainingRepository.findTrainingsByFilters(username, fromDate, toDate, trainerName, trainingType);
        return trainings.stream()
                .map(TrainingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateTraineeActiveStatus(String username, boolean isActive) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        trainee.setActive(isActive);
        traineeRepository.save(trainee);

        log.info("Updated trainee with username: {} to active status: {}", username, isActive);
    }
}