package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private UserGenerationUtilities userGenerationUtilities;


    @GetMapping("/{id}")
    public ResponseEntity<Trainee> getTrainee(@PathVariable Long id,
                                              @RequestHeader("username") String authUsername,
                                              @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainee by ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainee trainee = traineeService.getTrainee(id);
            if (trainee == null) {
                log.warn("No trainee found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.info("Returned trainee with ID: {}", id);
            return ResponseEntity.ok(trainee);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }


    @GetMapping
    public ResponseEntity<List<Trainee>> getAllTrainees(@RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to get all trainees");
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            List<Trainee> trainees = traineeService.getAllTrainees();
            log.info("Returned {} trainees", trainees.size());
            return ResponseEntity.ok(trainees);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Trainee> getTraineeByUsername(@PathVariable String username,
                                                        @RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainee by username: {}", username);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainee trainee = traineeService.getTraineeByUsername(username);
            if (trainee == null) {
                log.warn("No trainee found with username: {}", username);
                return ResponseEntity.notFound().build();
            }
            log.info("Returned trainee with username: {}", username);
            return ResponseEntity.ok(trainee);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @PostMapping
    public ResponseEntity<?> createTrainee(@RequestBody Trainee trainee) {
        try {
            log.debug("Received request to create a new trainee: {}", trainee);
            Trainee createdTrainee = traineeService.createTrainee(trainee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainee);
        } catch (IllegalArgumentException e) {
            log.error("Error creating trainee: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating trainee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Trainee> updateTrainee(@PathVariable Long id,
                                                 @RequestBody Trainee trainee,
                                                 @RequestHeader("username") String authUsername,
                                                 @RequestHeader("password") String authPassword) {
        log.debug("Received request to update trainee with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainee updatedTrainee = traineeService.updateTrainee(id, trainee);
            if (updatedTrainee == null) {
                log.error("Failed to find trainee with ID: {} for update", id);
                return ResponseEntity.notFound().build();
            }
            log.info("Successfully updated trainee with ID: {}", id);
            return ResponseEntity.ok(updatedTrainee);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changeTraineePassword(@PathVariable Long id,
                                                      @RequestBody Map<String, String> request,
                                                      @RequestHeader("username") String authUsername,
                                                      @RequestHeader("password") String authPassword) {
        log.debug("Received request to change password for trainee with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                String newPassword = request.get("newPassword");
                traineeService.changeTraineePassword(id, newPassword);
                log.info("Trainee with ID: {} has changed their password", id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to change password for trainee with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeByUsername(@PathVariable String username,
                                                        @RequestHeader("username") String authUsername,
                                                        @RequestHeader("password") String authPassword) {
        log.debug("Received request to delete trainee with username: {}", username);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                traineeService.deleteTraineeByUsername(username);
                log.info("Successfully deleted trainee with username: {}", username);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to delete trainee with username: {}", username, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateTrainee(@PathVariable Long id,
                                                @RequestParam boolean isActive,
                                                @RequestHeader("username") String authUsername,
                                                @RequestHeader("password") String authPassword) {
        log.debug("Received request to activate/deactivate trainee with ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            try {
                traineeService.activateTrainee(id, isActive);
                log.info("Trainee with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to activate/deactivate trainee with ID: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}