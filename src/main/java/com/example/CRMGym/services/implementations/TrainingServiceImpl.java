package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.TrainingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private final TrainingRepository trainingRepository;
    @Autowired
    private final TraineeRepository traineeRepository;
    @Autowired
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


//
//    @Override
//    public Training updateTraining(Long trainingId, Training updatedTraining) {
//        log.debug("Updating training ID: {}", trainingId);
//        Training training = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new RuntimeException("Training not found with ID: " + trainingId));
//        training.setTrainingName(updatedTraining.getTrainingName());
//        training.setTrainingType(updatedTraining.getTrainingType());
//        training.setTrainingDate(updatedTraining.getTrainingDate());
//        training.setTrainingDuration(updatedTraining.getTrainingDuration());
//        trainingRepository.save(training);
//        log.info("Training updated successfully for ID: {}", training.getId());
//        return training;
//    }
//
//    @Override
//    public void deleteTraining(Long trainingId) {
//        log.debug("Deleting training with ID: {}", trainingId);
//        Training training = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new RuntimeException("Training not found with ID: " + trainingId));
//        trainingRepository.delete(training);
//        log.info("Training deleted successfully with ID: {}", trainingId);
//    }
//
//    @Override
//    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
//        log.debug("Retrieving unassigned trainers for trainee: {}", traineeUsername);
//        String jpql = "SELECT tr FROM Trainer tr WHERE tr.id NOT IN (SELECT t.trainer.id FROM Training t WHERE t.trainee.username = :username)";
//        TypedQuery<Trainer> query = entityManager.createQuery(jpql, Trainer.class);
//        query.setParameter("username", traineeUsername);
//        List<Trainer> trainers = query.getResultList();
//        log.info("Retrieved {} unassigned trainers for trainee: {}", trainers.size(), traineeUsername);
//        return trainers;
//    }
//    @Override
//    public void updateTraineeTrainersList(String traineeUsername, List<Long> trainerIds) {
//        log.debug("Updating trainers list for trainee: {}", traineeUsername);
//        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
//                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + traineeUsername));
//        List<Trainer> trainers = trainerRepository.findAllById(trainerIds);
//        List<Training> trainings = new ArrayList<>();
//        for (Trainer trainer : trainers) {
//            Training training = new Training();
//            training.setTrainer(trainer);
//            training.setTrainee(trainee);
//            training.setTrainingDate(LocalDateTime.now());
//            trainings.add(training);
//        }
//        trainingRepository.saveAll(trainings);
//        log.info("Updated trainers list for trainee: {}", traineeUsername);
//    }