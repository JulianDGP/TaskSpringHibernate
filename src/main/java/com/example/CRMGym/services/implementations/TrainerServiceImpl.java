package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.services.TrainerService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final UserGenerationUtilities userGenerationUtilities;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository, PasswordEncoder passwordEncoder, UserGenerationUtilities userGenerationUtilities) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.userGenerationUtilities = userGenerationUtilities;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        log.debug("Retrieving all trainers");
        return trainerRepository.findAll();
    }

    @Override
    public Trainer getTrainer(Long id) {
        log.debug("Retrieving Trainer by ID: {}", id);
        return trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + id));
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        log.debug("Retrieving Trainer by username: {}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
    }

    @Override
    @Transactional
    public Trainer createTrainer(Trainer trainer) {
        log.debug("Creating Trainer: {}", trainer);
        String username = userGenerationUtilities.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = userGenerationUtilities.generateRandomPassword();

        log.info("This is the password for testing: {}", password);
        String encodedPassword = passwordEncoder.encode(password);

        trainer.setUsername(username);
        trainer.setPassword(encodedPassword);
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer created successfully with ID: {}", savedTrainer.getId());
        return savedTrainer;
    }

    public Trainer updateTrainer(Long id, Trainer trainer) {
        log.debug("Updating Trainer with ID: {}", id);
        Trainer savedTrainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + id));
        savedTrainer.setFirstName(trainer.getFirstName());
        savedTrainer.setLastName(trainer.getLastName());
        savedTrainer.setSpecialization(trainer.getSpecialization());
        return trainerRepository.save(savedTrainer);
    }

    @Override
    public void changeTrainerPassword(Long id, String newPassword) {
        log.debug("Changing password for Trainer with ID: {}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + id));
        log.info("This is the changed newPassword for testing: {}", newPassword);
        String encodedPassword = passwordEncoder.encode(newPassword);
        trainer.setPassword(encodedPassword);
        trainerRepository.save(trainer);
        log.info("Trainer with ID: {} has changed their password", id);
    }
    @Override
    public void deleteTrainerByUsername(String username) {
        log.debug("Deleting Trainer with username: {}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
        trainerRepository.delete(trainer);
        log.info("Trainer with username: {} has been deleted", username);
    }

    @Override
    public void activateTrainer(Long id, boolean isActive) {
        log.debug("Activating/Deactivating Trainer with ID: {}", id);
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + id));
        trainer.setActive(isActive);
        trainerRepository.save(trainer);
        log.info("Trainer with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
    }

}
