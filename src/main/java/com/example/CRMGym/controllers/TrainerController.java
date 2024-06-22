package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TrainerMapper;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.services.TrainerService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;

    private final UserGenerationUtilities userGenerationUtilities;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerController(TrainerService trainerService, UserGenerationUtilities userGenerationUtilities, PasswordEncoder passwordEncoder) {
        this.trainerService = trainerService;
        this.userGenerationUtilities = userGenerationUtilities;
        this.passwordEncoder = passwordEncoder;
    }

    /*2.Trainer Registration with POST method */
    @Operation(summary = "Register Trainer", description = "Register a new trainer")
    @PostMapping("/register")
    public ResponseEntity<?> registerTrainer(@Valid @RequestBody TrainerDTO trainerDTO) {
        log.info("Received request to create a new trainer: {}", trainerDTO);
        try {
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
            log.info("Trainer created successfully: {}", createdTrainer);
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
    @Operation(summary = "Get Trainer Profile", description = "Get trainer profile", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getTrainerProfile(@PathVariable String username) {
        log.info("Received request to get profile for trainer: {}", username);
        try {
            TrainerProfileDTO trainerProfileDTO = trainerService.getTrainerProfile(username);
            log.info("Returned profile for trainer: {}", username);
            return ResponseEntity.ok(trainerProfileDTO);
        } catch (Exception e) {
            log.error("Trainer not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 9. Update Trainer Profile with PUT method */
    @Operation(summary = "Update Trainer Profile", description = "Update trainer profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateTrainerProfile(@PathVariable String username, @RequestBody TrainerDTO trainerDTO) {
        log.info("Received request to update profile for trainer: {}", username);
        try {
            TrainerProfileDTO updatedTrainerProfileDTO = trainerService.updateTrainerProfile(username, trainerDTO);
            log.info("Profile updated successfully for trainer: {}", username);
            return ResponseEntity.ok(updatedTrainerProfileDTO);
        } catch (Exception e) {
            log.error("Trainer not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 13. Get Trainer Trainings List with GET method */
    @Operation(summary = "Get Trainer Trainings List", description = "Get list of trainings for a trainer", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/trainings")
    public ResponseEntity<?> getTrainerTrainings(@RequestParam String username,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                                 @RequestParam(required = false) String traineeName) {
        log.info("Received request to get trainings for trainer: {}", username);
        try {
            List<TrainingDTO> trainings = trainerService.getTrainerTrainings(username, fromDate, toDate, traineeName);
            log.info("Returned trainings for trainer: {}", username);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            log.error("Trainer or Trainings not found for trainer: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer or Trainings not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 16. Activate/De-Activate Trainer with PATCH method */
    @Operation(summary = "Activate/De-Activate Trainer", description = "Activate or de-activate a trainer", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/activate")
    public ResponseEntity<?> updateTrainerActiveStatus(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        boolean isActive = (Boolean) payload.get("isActive");
        log.info("Received request to update active status for trainer: {}", username);
        try {
            if (username == null || username.isBlank()) {
                log.warn("Username is required for updating active status");
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required.", HttpStatus.BAD_REQUEST.value()));
            }

            trainerService.updateTrainerActiveStatus(username, isActive);
            log.info("Active status updated successfully for trainer: {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating active status for trainer with username: {}", payload.get("username"), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainer not found", HttpStatus.NOT_FOUND.value()));
        }
    }
}
