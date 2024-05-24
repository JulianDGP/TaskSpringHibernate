package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private final TraineeRepository traineeRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserGenerationUtilities userGenerationUtilities;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, PasswordEncoder passwordEncoder, UserGenerationUtilities userGenerationUtilities) {
        this.traineeRepository = traineeRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGenerationUtilities = userGenerationUtilities;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.debug("Retrieving all trainees");
        return traineeRepository.findAll();
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
    @Transactional
    public Trainee createTrainee(Trainee trainee) {
        log.debug("Creating Trainee: {}", trainee);
        String username = userGenerationUtilities.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = userGenerationUtilities.generateRandomPassword();

        log.info("This is the password for testing: {}", password);
        String encodedPassword = passwordEncoder.encode(password);

        trainee.setUsername(username);
        trainee.setPassword(encodedPassword);
        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Trainee created successfully with ID: {}", savedTrainee.getId());
        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Long id, Trainee trainee) {
        log.debug("Updating Trainee with ID: {}", id);
        Trainee savedTrainee = traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
        savedTrainee.setFirstName(trainee.getFirstName());
        savedTrainee.setLastName(trainee.getLastName());
        savedTrainee.setDateOfBirth(trainee.getDateOfBirth());
        savedTrainee.setAddress(trainee.getAddress());

        return traineeRepository.save(savedTrainee);
    }

    @Override
    public void changeTraineePassword(Long id, String newPassword) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
        log.info("This is the changed newPassword for testing: {}", newPassword);
        String encodedPassword = passwordEncoder.encode(newPassword);
        trainee.setPassword(encodedPassword);
        traineeRepository.save(trainee);
        log.info("Trainee with ID: {} has changed their password", id);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.debug("Deleting Trainee with username: {}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + username));
        traineeRepository.delete(trainee);
        log.info("Trainee with username: {} has been deleted", username);
    }



    @Override
    public void activateTrainee(Long id, boolean isActive) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
        trainee.setActive(isActive);
        traineeRepository.save(trainee);
        log.info("Trainee with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
    }
}
