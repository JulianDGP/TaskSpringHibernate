package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.services.TrainingService;
import com.example.CRMGym.services.implementations.TrainingServiceImpl;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private UserGenerationUtilities userGenerationUtilities;


    @GetMapping("/{id}")
    public ResponseEntity<Training> getTraining(@PathVariable Long id,
                                                @RequestHeader("username") String username,
                                                @RequestHeader("password") String password) {
        log.debug("Received request to get training by ID: {}", id);
        if (userGenerationUtilities.checkCredentials(username, password)) {
            Training training = trainingService.getTraining(id);
            if (training == null) {
                log.warn("No training found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.info("Returned training with ID: {}", id);
            return ResponseEntity.ok(training);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @PostMapping
    public ResponseEntity<?> createTraining(@RequestBody Training training,
                                            @RequestHeader("username") String username,
                                            @RequestHeader("password") String password) {
        if (userGenerationUtilities.checkCredentials(username, password)) {
            try {
                log.debug("Received request to create a new training: {}", training);
                Training createdTraining = trainingService.createTraining(training);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdTraining);
            } catch (IllegalArgumentException e) {
                log.error("Error creating training: {}", e.getMessage(), e);
                return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
            } catch (Exception e) {
                log.error("Unexpected error occurred while creating training: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateTraining(@PathVariable Long id,
                                                   @RequestBody Training training,
                                                   @RequestHeader("username") String username,
                                                   @RequestHeader("password") String password) {
        log.debug("Received request to update training with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(username, password)) {
            try {
                trainingService.updateTraining(id, training);
                log.info("Successfully updated training with ID: {}", id);
                return ResponseEntity.ok(training);
            } catch (Exception e) {
                log.error("Failed to update training with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                        body(new ErrorResponse("An unexpected error occurred",
                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id,
                                               @RequestHeader("username") String username,
                                               @RequestHeader("password") String password) {
        log.debug("Received request to delete training with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(username, password)) {
            try {
                trainingService.deleteTraining(id);
                log.info("Successfully deleted training with ID: {}", id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to delete training with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/trainee/{username}")
    public ResponseEntity<?> getTraineeTrainings(@PathVariable String username,
                                                              @RequestParam LocalDate fromDate,
                                                              @RequestParam LocalDate toDate,
                                                              @RequestParam(required = false) String trainerName,
                                                              @RequestParam(required = false) String trainingType,
                                                              @RequestHeader("username") String authUsername,
                                                              @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainings for trainee: {}", username);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                List<Training> trainings = trainingService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
                log.info("Returned {} trainings for trainee: {}", trainings.size(), username);
                return ResponseEntity.ok(trainings);
            } catch (Exception e) {
                log.error("Failed to retrieve trainings for trainee: {}", username, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                        body(new ErrorResponse("An unexpected error occurred",
                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/trainer/{username}")
    public ResponseEntity<?> getTrainerTrainings(@PathVariable String username,
                                                              @RequestParam LocalDate fromDate,
                                                              @RequestParam LocalDate toDate,
                                                              @RequestParam(required = false) String traineeName,
                                                              @RequestHeader("username") String authUsername,
                                                              @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainings for trainer: {}", username);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                List<Training> trainings = trainingService.getTrainerTrainings(username, fromDate, toDate, traineeName);
                log.info("Returned {} trainings for trainer: {}", trainings.size(), username);
                return ResponseEntity.ok(trainings);
            } catch (Exception e) {
                log.error("Failed to retrieve trainings for trainer: {}", username, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/unassigned-trainers/{traineeUsername}")
    public ResponseEntity<?> getUnassignedTrainers(@PathVariable String traineeUsername,
                                                               @RequestHeader("username") String authUsername,
                                                               @RequestHeader("password") String authPassword) {
        log.debug("Received request to get unassigned trainers for trainee: {}", traineeUsername);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                List<Trainer> trainers = trainingService.getUnassignedTrainers(traineeUsername);
                log.info("Returned {} unassigned trainers for trainee: {}", trainers.size(), traineeUsername);
                return ResponseEntity.ok(trainers);
            } catch (Exception e) {
                log.error("Failed to retrieve unassigned trainers for trainee: {}", traineeUsername, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}

//    @PutMapping("/update-trainers-list/{traineeUsername}")
//    public ResponseEntity<Void> updateTraineeTrainersList(@PathVariable String traineeUsername,
//                                                          @RequestBody List<Long> trainerIds,
//                                                          @RequestHeader("username") String authUsername,
//                                                          @RequestHeader("password") String authPassword) {
//        log.debug("Received request to update trainers list for trainee: {}", traineeUsername);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                trainingService.updateTraineeTrainersList(traineeUsername, trainerIds);
//                log.info("Updated trainers list for trainee: {}", traineeUsername);
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to update trainers list for trainee: {}", traineeUsername, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }


