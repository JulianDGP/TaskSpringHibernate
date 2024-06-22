package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.TrainingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional
    public Training createTraining(TrainingRequestDTO trainingRequestDTO) {
        Trainee trainee = traineeRepository.findByUsername(trainingRequestDTO.traineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + trainingRequestDTO.traineeUsername()));
        Trainer trainer = trainerRepository.findByUsername(trainingRequestDTO.trainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + trainingRequestDTO.trainerUsername()));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingRequestDTO.trainingName());
        training.setTrainingDate(trainingRequestDTO.trainingDate());
        training.setTrainingDuration(trainingRequestDTO.trainingDuration());

        log.debug("Creating training: {}", training);
        Training savedTraining = trainingRepository.save(training);
        log.info("Training created successfully with ID: {}", savedTraining.getId());
        return savedTraining;
    }

    @Override
    @Transactional(readOnly = true)
    public Training getTraining(Long id) {
        log.debug("Retrieving training by ID: {}", id);
        return trainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found with ID: " + id));
    }
}