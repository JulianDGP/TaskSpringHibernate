package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TrainerMapper;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.services.TrainerService;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private UserGenerationUtilities userGenerationUtilities;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*2.Trainer Registration with POST method */
    @PostMapping("/register")
    public ResponseEntity<?> registerTrainer(@RequestBody TrainerDTO trainerDTO) {
        try {
            log.debug("Received request to create a new trainer: {}", trainerDTO);

            // Required fields validation
            if (trainerDTO.firstName() == null || trainerDTO.firstName().isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("First Name is required.", HttpStatus.BAD_REQUEST.value()));
            }
            if (trainerDTO.lastName() == null || trainerDTO.lastName().isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Last Name is required.", HttpStatus.BAD_REQUEST.value()));
            }
            if (trainerDTO.specialization() == null || trainerDTO.specialization().isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Specialization is required.", HttpStatus.BAD_REQUEST.value()));
            }

            // Convert DTO to entity
            Trainer trainer = TrainerMapper.toEntity(trainerDTO);

            // Generate Username and Password
            String username = userGenerationUtilities.generateUsername(trainer.getFirstName(), trainer.getLastName());
            String password = userGenerationUtilities.generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(password);
            trainer.setUsername(username);
            trainer.setPassword(encodedPassword);


            // Save Trainer
            Trainer createdTrainer = trainerService.createTrainer(trainer);

            // Generate Response with Username y Password
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "username", username,
                    "password", password
            ));
            //Error handling
        } catch (IllegalArgumentException e) {
            log.error("Error creating trainer: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating trainer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /* 8. Get Trainer Profile with trainees with GET method */
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getTrainerProfile(@PathVariable String username) {
        try {
            TrainerProfileDTO trainerProfileDTO = trainerService.getTrainerProfile(username);
            return ResponseEntity.ok(trainerProfileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 9. Update Trainer Profile with PUT method */
    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateTrainerProfile(@PathVariable String username, @RequestBody TrainerDTO trainerDTO) {
        try {
            TrainerProfileDTO updatedTrainerProfileDTO = trainerService.updateTrainerProfile(username, trainerDTO);
            return ResponseEntity.ok(updatedTrainerProfileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 13. Get Trainer Trainings List with GET method */
    @GetMapping("/trainings")
    public ResponseEntity<?> getTrainerTrainings(@RequestParam String username,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                                 @RequestParam(required = false) String traineeName) {
        try {
            List<TrainingDTO> trainings = trainerService.getTrainerTrainings(username, fromDate, toDate, traineeName);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer or Trainings not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 16. Activate/De-Activate Trainer with PATCH method */
    @PatchMapping("/activate")
    public ResponseEntity<?> updateTrainerActiveStatus(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            boolean isActive = (Boolean) payload.get("isActive");

            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required.", HttpStatus.BAD_REQUEST.value()));
            }

            trainerService.updateTrainerActiveStatus(username, isActive);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating active status for trainer with username: {}", payload.get("username"), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }


}

//
//    @GetMapping("/{id}")
//    public ResponseEntity<TrainerDTO> getTrainer(@PathVariable Long id,
//                                                 @RequestHeader("username") String authUsername,
//                                                 @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get trainer by ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainer trainer = trainerService.getTrainer(id);
//            if (trainer == null) {
//                log.warn("No trainer found with ID: {}", id);
//                return ResponseEntity.notFound().build();
//            }
//            TrainerDTO trainerDTO = TrainerMapper.toDTO(trainer);
//            log.info("Returned trainer with ID: {}", id);
//            return ResponseEntity.ok(trainerDTO);
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<TrainerDTO>> getAllTrainers(@RequestHeader("username") String authUsername,
//                                                           @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get all trainers");
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            List<Trainer> trainers = trainerService.getAllTrainers();
//            List<TrainerDTO> trainerDTOs = trainers.stream()
//                    .map(TrainerMapper::toDTO)
//                    .toList();
//            log.info("Returned {} trainers", trainerDTOs.size());
//            return ResponseEntity.ok(trainerDTOs);
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//
//    @GetMapping("/username/{username}")
//    public ResponseEntity<TrainerDTO> getTrainerByUsername(@PathVariable String username,
//                                                           @RequestHeader("username") String authUsername,
//                                                           @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get trainer by username: {}", username);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainer trainer = trainerService.getTrainerByUsername(username);
//            if (trainer == null) {
//                log.warn("No trainer found with username: {}", username);
//                return ResponseEntity.notFound().build();
//            }
//            TrainerDTO trainerDTO = TrainerMapper.toDTO(trainer);
//            log.info("Returned trainer with username: {}", username);
//            return ResponseEntity.ok(trainerDTO);
//        }
//        log.warn("Unauthorized access attempt with username: {}", authUsername);
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//
//
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<TrainerDTO> updateTrainer(@PathVariable Long id,
//                                                 @RequestBody TrainerDTO trainerDTO,
//                                                 @RequestHeader("username") String authUsername,
//                                                 @RequestHeader("password") String authPassword) {
//        log.debug("Received request to update trainer with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainer trainer = TrainerMapper.toEntity(trainerDTO);
//            trainer.setId(id);
//            Trainer updatedTrainer = trainerService.updateTrainer(id, trainer);
//            if (updatedTrainer == null) {
//                log.error("Failed to find trainer with ID: {} for update", id);
//                return ResponseEntity.notFound().build();
//            }
//            TrainerDTO updatedTrainerDTO = TrainerMapper.toDTO(updatedTrainer);
//            log.info("Successfully updated trainer with ID: {}", id);
//            return ResponseEntity.ok(updatedTrainerDTO);
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PutMapping("/{id}/change-password")
//    public ResponseEntity<Void> changeTrainerPassword(@PathVariable Long id,
//                                                      @RequestBody Map<String, String> request,
//                                                      @RequestHeader("username") String authUsername,
//                                                      @RequestHeader("password") String authPassword) {
//        log.debug("Received request to change password for trainer with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                String newPassword = request.get("newPassword");
//                trainerService.changeTrainerPassword(id, newPassword);
//                log.info("Trainer with ID: {} has changed their password", id);
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to change password for trainer with ID: {}", id, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//
//
//    @DeleteMapping("/{username}")
//    public ResponseEntity<Void> deleteTrainerByUsername(@PathVariable String username,
//                                                        @RequestHeader("username") String authUsername,
//                                                        @RequestHeader("password") String authPassword) {
//        log.debug("Received request to delete trainer with username: {}", username);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                trainerService.deleteTrainerByUsername(username);
//                log.info("Successfully deleted trainer with username: {}", username);
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to delete trainer with username: {}", username, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//
//    @PutMapping("/activate/{id}")
//    public ResponseEntity<Void> activateTrainer(@PathVariable Long id,
//                                                @RequestParam boolean isActive,
//                                                @RequestHeader("username") String authUsername,
//                                                @RequestHeader("password") String authPassword) {
//        log.debug("Received request to activate/deactivate trainer with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                trainerService.activateTrainer(id, isActive);
//                log.info("Trainer with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to activate/deactivate trainer with ID: {}", id, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
