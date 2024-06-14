package com.example.CRMGym.services.implementations;

import com.example.CRMGym.mappers.TrainerMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.services.TrainingService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private final TraineeRepository traineeRepository;

    @Autowired
    private final TrainingService trainingService;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainingService trainingService) {
        this.traineeRepository = traineeRepository;
        this.trainingService = trainingService;
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
    public Trainee getTrainee(Long id) {
        log.debug("Retrieving Trainee with ID: {}", id);
        return traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        log.debug("Retrieving Trainee by username: {}", username);
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));
    }
    @Override
    public TraineeProfileDTO getTraineeProfile(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));

        Set<Training> trainings = trainingService.findTrainingsByTraineeId(trainee.getId());
        Set<Trainer> trainers = trainings.stream()
                .map(Training::getTrainer)
                .collect(Collectors.toSet());

        List<TrainerDTO> trainerDTOs = trainers.stream()
                .map(TrainerMapper::toDTO)
                .collect(Collectors.toList());

        return new TraineeProfileDTO(
                trainee.getId(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getUsername(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive(),
                trainerDTOs
        );
    }
}


//    @Override
//    public List<Trainee> getAllTrainees() {
//        log.debug("Retrieving all trainees");
//        return traineeRepository.findAll();
//    }

//    @Override
//    public Trainee updateTrainee(Long id, Trainee trainee) {
//        log.debug("Updating Trainee with ID: {}", id);
//        Trainee savedTrainee = traineeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
//        savedTrainee.setFirstName(trainee.getFirstName());
//        savedTrainee.setLastName(trainee.getLastName());
//        savedTrainee.setDateOfBirth(trainee.getDateOfBirth());
//        savedTrainee.setAddress(trainee.getAddress());
//
//        return traineeRepository.save(savedTrainee);
//    }

//    @Override
//    public void changeTraineePassword(Long id, String newPassword) {
//        Trainee trainee = traineeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
//        log.info("This is the changed newPassword for testing: {}", newPassword);
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        trainee.setPassword(encodedPassword);
//        traineeRepository.save(trainee);
//        log.info("Trainee with ID: {} has changed their password", id);
//    }

//    @Override
//    public void deleteTraineeByUsername(String username) {
//        log.debug("Deleting Trainee with username: {}", username);
//        Trainee trainee = traineeRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));
//        traineeRepository.delete(trainee);
//        log.info("Trainee with username: {} has been deleted", username);
//    }
//
//
//
//    @Override
//    public void activateTrainee(Long id, boolean isActive) {
//        Trainee trainee = traineeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
//        trainee.setActive(isActive);
//        traineeRepository.save(trainee);
//        log.info("Trainee with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
//    }
