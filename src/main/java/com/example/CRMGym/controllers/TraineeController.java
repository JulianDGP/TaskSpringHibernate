package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TraineeMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.monitoring.CustomMetrics;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Trainee", description = "Operations related to trainees")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);
    private final TraineeService traineeService;
    private final UserGenerationUtilities userGenerationUtilities;
    private final PasswordEncoder passwordEncoder;
    private final CustomMetrics customMetrics;


    @Autowired
    public TraineeController(TraineeService traineeService, UserGenerationUtilities userGenerationUtilities, PasswordEncoder passwordEncoder, CustomMetrics customMetrics) {
        this.traineeService = traineeService;
        this.userGenerationUtilities = userGenerationUtilities;
        this.passwordEncoder = passwordEncoder;
        this.customMetrics = customMetrics;
    }

    /* 1.Trainee Registration with POST method */
    @Operation(summary = "Register a new trainee")
    @PostMapping("/register")
    public ResponseEntity<?> registerTrainee(@Valid @RequestBody TraineeDTO traineeDTO) {
        long startTime = System.currentTimeMillis();
        try {
            log.debug("Received request to create a new trainee: {}", traineeDTO);
            // Convert DTO to entity
            Trainee trainee = TraineeMapper.toEntity(traineeDTO);
            // Generate Username and Password
            String username = userGenerationUtilities.generateUsername(trainee.getFirstName(), trainee.getLastName());
            String password = userGenerationUtilities.generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(password);
            trainee.setUsername(username);
            trainee.setPassword(encodedPassword);
            // Save Trainee
            Trainee createdTrainee = traineeService.createTrainee(trainee);
            log.info("Trainee created successfully: {}", createdTrainee);

            // Record trainee registration time
            long duration = System.currentTimeMillis() - startTime;
            customMetrics.recordTraineeRegistrationTime(duration);

            // Create response map with correct order
            Map<String, String> responseMap = new LinkedHashMap<>();
            responseMap.put("username", username);
            responseMap.put("password", password);

            // Generate Response with Username y Password
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
            //Error handling
        } catch (IllegalArgumentException e) {
            log.error("Error creating trainee: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating trainee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    /* 5. Get Trainee Profile with trainers with GET method */
    @Operation(summary = "Get trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getTraineeProfile(@PathVariable String username) {
        log.info("Received request to get profile for trainee: {}", username);
        try {
            TraineeProfileDTO traineeProfileDTO = traineeService.getTraineeProfile(username);
            log.info("Returned profile for trainee: {}", username);
            return ResponseEntity.ok(traineeProfileDTO);
        } catch (Exception e) {
            log.error("Trainee not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 6. Update Trainee Profile with PUT method */

    @Operation(summary = "Update trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateTraineeProfile(@PathVariable String username, @RequestBody TraineeDTO traineeDTO) {
        log.info("Received request to update profile for trainee: {}", username);
        try {
            TraineeProfileDTO updatedTraineeProfileDTO = traineeService.updateTraineeProfile(username, traineeDTO);
            log.info("Profile updated successfully for trainee: {}", username);
            return ResponseEntity.ok(updatedTraineeProfileDTO);
        } catch (Exception e) {
            log.error("Trainee not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 7. Delete Trainee Profile with DELETE method */
    @Operation(summary = "Delete trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/profile/{username}")
    public ResponseEntity<?> deleteTraineeProfile(@PathVariable String username) {
        log.info("Received request to delete profile for trainee: {}", username);
        try {
            traineeService.deleteTraineeByUsername(username);
            log.info("Trainee profile deleted successfully: {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Trainee not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 10.Get not assigned on trainee active trainers. */
    @Operation(summary = "Get not assigned active trainers for a trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/not-assigned/{username}")
    public ResponseEntity<?> getNotAssignedActiveTrainers(@PathVariable String username) {
        log.info("Received request to get not assigned active trainers for trainee: {}", username);
        try {
            List<TrainerDTO> trainers = traineeService.getNotAssignedActiveTrainers(username);
            log.info("Returned not assigned active trainers for trainee: {}", username);
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
            log.error("Trainee not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 12. Get Trainee Trainings List with GET method */
    @Operation(summary = "Get trainee's training list", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/trainings")
    public ResponseEntity<?> getTraineeTrainings(@RequestParam String username,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                                 @RequestParam(required = false) String trainerName,
                                                 @RequestParam(required = false) String trainingType) {
        log.info("Received request to get trainings for trainee: {}", username);
        try {
            List<TrainingDTO> trainings = traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
            log.info("Returned trainings for trainee: {}", username);
            return ResponseEntity.ok(trainings);
        } catch (RuntimeException e) {
            log.error("Trainee or Trainings not found for trainee: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee or Trainings not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 15. Activate/De-Activate Trainee with PATCH method */
    @Operation(summary = "Activate/De-Activate trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/activate")
    public ResponseEntity<?> updateTraineeActiveStatus(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            boolean isActive = (Boolean) payload.get("isActive");
            log.info("Received request to update active status for trainee: {}", username);
            if (username == null || username.isBlank()) {
                log.warn("Username is required for updating active status");
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required.", HttpStatus.BAD_REQUEST.value()));
            }

            traineeService.updateTraineeActiveStatus(username, isActive);
            log.info("Active status updated successfully for trainee: {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating active status for trainee with username: {}", payload.get("username"), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }
}

