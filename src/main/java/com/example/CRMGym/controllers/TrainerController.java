package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.services.TrainerService;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private UserGenerationUtilities userGenerationUtilities;

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainer(@PathVariable Long id,
                                              @RequestHeader("username") String authUsername,
                                              @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainer by ID: {}", id);
        log.debug("Credentials received - Username: {}, Password: {}", authUsername, authPassword);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainer trainer = trainerService.getTrainer(id);
            if (trainer == null) {
                log.warn("No trainer found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.info("Returned trainer with ID: {}", id);
            return ResponseEntity.ok(trainer);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers(@RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to get all trainers");
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            List<Trainer> trainers = trainerService.getAllTrainers();
            log.info("Returned {} trainers", trainers.size());
            return ResponseEntity.ok(trainers);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Trainer> getTrainerByUsername(@PathVariable String username,
                                                        @RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainer by username: {}", username);
        log.debug("Credentials received - Username: {}, Password: {}", authUsername, authPassword);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainer trainer = trainerService.getTrainerByUsername(username);
            if (trainer == null) {
                log.warn("No trainer found with username: {}", username);
                return ResponseEntity.notFound().build();
            }
            log.info("Returned trainer with username: {}", username);
            return ResponseEntity.ok(trainer);
        }
        log.warn("Unauthorized access attempt with username: {}", authUsername);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping
    public ResponseEntity<?> createTrainer(@RequestBody Trainer trainer) {
        try {
            log.debug("Received request to create a new trainer: {}", trainer);
            Trainer createdTrainer = trainerService.createTrainer(trainer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainer);
        } catch (IllegalArgumentException e) {
            log.error("Error creating trainer: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating trainer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id,
                                                 @RequestBody Trainer trainer,
                                                 @RequestHeader("username") String authUsername,
                                                 @RequestHeader("password") String authPassword) {
        log.debug("Received request to update trainer with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainer updatedTrainer = trainerService.updateTrainer(id, trainer);
            if (updatedTrainer == null) {
                log.error("Failed to find trainer with ID: {} for update", id);
                return ResponseEntity.notFound().build();
            }
            log.info("Successfully updated trainer with ID: {}", id);
            return ResponseEntity.ok(updatedTrainer);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changeTrainerPassword(@PathVariable Long id,
                                                      @RequestBody Map<String, String> request,
                                                      @RequestHeader("username") String authUsername,
                                                      @RequestHeader("password") String authPassword) {
        log.debug("Received request to change password for trainer with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                String newPassword = request.get("newPassword");
                trainerService.changeTrainerPassword(id, newPassword);
                log.info("Trainer with ID: {} has changed their password", id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to change password for trainer with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainerByUsername(@PathVariable String username,
                                                        @RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to delete trainer with username: {}", username);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                trainerService.deleteTrainerByUsername(username);
                log.info("Successfully deleted trainer with username: {}", username);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to delete trainer with username: {}", username, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateTrainer(@PathVariable Long id,
                                                @RequestParam boolean isActive,
                                                @RequestHeader("username") String authUsername,
                                                @RequestHeader("password") String authPassword) {
        log.debug("Received request to activate/deactivate trainer with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                trainerService.activateTrainer(id, isActive);
                log.info("Trainer with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to activate/deactivate trainer with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
