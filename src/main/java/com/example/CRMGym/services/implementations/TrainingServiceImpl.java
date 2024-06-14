package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
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
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }


    @Override
    public Set<Training> findTrainingsByTraineeId(Long traineeId) {
        return trainingRepository.findByTraineeId(traineeId);
    }
}
//
//    @Override
//    @Transactional
//    public Training createTraining(Training training) {
//        log.debug("Creating training: {}", training);
//        Training savedTraining = trainingRepository.save(training);
//        log.info("Training created successfully with ID: {}", savedTraining.getId());
//        return savedTraining;
//    }
//
//    @Override
//    public Training getTraining(Long id) {
//        log.debug("Retrieving training by ID: {}", id);
//        return trainingRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Training not found with ID: " + id));
//    }
//
//    @Override
//    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {
//
//        log.debug("Retrieving trainings for trainee: {}", traineeUsername);
//        String jpql = "SELECT t FROM Training t WHERE t.trainee.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate";
//
//        if (trainerName != null && !trainerName.isEmpty()) {
//            jpql += " AND t.trainer.name = :trainerName";
//        }
//        if (trainingType != null && !trainingType.isEmpty()) {
//            jpql += " AND t.trainingType = :trainingType";
//        }
//
//        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
//        query.setParameter("username", traineeUsername);
//        query.setParameter("fromDate", fromDate.atStartOfDay());
//        query.setParameter("toDate", toDate.atTime(23, 59, 59));
//
//        if (trainerName != null && !trainerName.isEmpty()) {
//            query.setParameter("trainerName", trainerName);
//        }
//        if (trainingType != null && !trainingType.isEmpty()) {
//            query.setParameter("trainingType", TrainingType.valueOf(trainingType));
//        }
//        log.info("Trainings retrieved successfully");
//        return query.getResultList();
//    }
//
//    @Override
//    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
//        log.debug("Retrieving trainings for trainer: {}", trainerUsername);
//        String jpql = "SELECT t FROM Training t WHERE t.trainer.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate";
//
//        if (traineeName != null && !traineeName.isEmpty()) {
//            jpql += " AND t.trainee.name = :traineeName";
//        }
//
//        TypedQuery<Training> query = entityManager.createQuery(jpql, Training.class);
//        query.setParameter("username", trainerUsername);
//        query.setParameter("fromDate", fromDate.atStartOfDay());
//        query.setParameter("toDate", toDate.atTime(23, 59));
//
//        if (traineeName != null && !traineeName.isEmpty()) {
//            query.setParameter("traineeName", traineeName);
//        }
//        return query.getResultList();
//    }
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