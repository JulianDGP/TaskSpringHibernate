package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TraineeMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Trainee", description = "Operations related to trainees")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private UserGenerationUtilities userGenerationUtilities;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* 1.Trainee Registration with POST method */
    @Operation(summary = "Register a new trainee")
    @PostMapping("/register")
    public ResponseEntity<?> registerTrainee(@RequestBody TraineeDTO traineeDTO) {
        try {
            log.debug("Received request to create a new trainee: {}", traineeDTO);
            // Required fields validation
            if (traineeDTO.firstName() == null || traineeDTO.firstName().isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("First Name is required.", HttpStatus.BAD_REQUEST.value()));
            }
            if (traineeDTO.lastName() == null || traineeDTO.lastName().isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Last Name is required.", HttpStatus.BAD_REQUEST.value()));
            }
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
            // Generate Response with Username y Password
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "username", username,
                    "password", password
            ));
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
        try {
            TraineeProfileDTO traineeProfileDTO = traineeService.getTraineeProfile(username);
            return ResponseEntity.ok(traineeProfileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 6. Update Trainee Profile with PUT method */

    @Operation(summary = "Update trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/profile/{username}")
    public ResponseEntity<?> updateTraineeProfile(@PathVariable String username, @RequestBody TraineeDTO traineeDTO) {
        try {
            TraineeProfileDTO updatedTraineeProfileDTO = traineeService.updateTraineeProfile(username, traineeDTO);
            return ResponseEntity.ok(updatedTraineeProfileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 7. Delete Trainee Profile with DELETE method */
    @Operation(summary = "Delete trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/profile/{username}")
    public ResponseEntity<?> deleteTraineeProfile(@PathVariable String username) {
        try {
            traineeService.deleteTraineeByUsername(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 10.Get not assigned on trainee active trainers. */
    @Operation(summary = "Get not assigned active trainers for a trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/not-assigned/{username}")
    public ResponseEntity<?> getNotAssignedActiveTrainers(@PathVariable String username) {
        try {
            List<TrainerDTO> trainers = traineeService.getNotAssignedActiveTrainers(username);
            return ResponseEntity.ok(trainers);
        } catch (Exception e) {
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
        try {
            List<TrainingDTO> trainings = traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
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

            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required.", HttpStatus.BAD_REQUEST.value()));
            }

            traineeService.updateTraineeActiveStatus(username, isActive);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating active status for trainee with username: {}", payload.get("username"), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }
}


//    @GetMapping("/{id}")
//    public ResponseEntity<TraineeDTO> getTrainee(@PathVariable Long id,
//                                                 @RequestHeader("username") String authUsername,
//                                                 @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get trainee by ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainee trainee = traineeService.getTrainee(id);
//            if (trainee == null) {
//                log.warn("No trainee found with ID: {}", id);
//                return ResponseEntity.notFound().build();
//            }
//            TraineeDTO traineeDTO = TraineeMapper.toDTO(trainee);
//            log.info("Returned trainee with ID: {}", id);
//            return ResponseEntity.ok(traineeDTO);
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

//    @GetMapping
//    public ResponseEntity<List<TraineeDTO>> getAllTrainees(@RequestHeader("username") String authUsername,
//                                                           @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get all trainees");
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            List<Trainee> trainees = traineeService.getAllTrainees();
//            List<TraineeDTO> traineeDTOs = trainees.stream()
//                    .map(TraineeMapper::toDTO)
//                    .toList();
//            log.info("Returned {} trainees", traineeDTOs.size());
//            return ResponseEntity.ok(traineeDTOs);
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

//    @GetMapping("/username/{username}")
//    public ResponseEntity<TraineeDTO> getTraineeByUsername(@PathVariable String username,
//                                                           @RequestHeader("username") String authUsername,
//                                                           @RequestHeader("password") String authPassword) {
//        log.debug("Received request to get trainee by username: {}", username);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainee trainee = traineeService.getTraineeByUsername(username);
//            if (trainee == null) {
//                log.warn("No trainee found with username: {}", username);
//                return ResponseEntity.notFound().build();
//            }
//            TraineeDTO traineeDTO = TraineeMapper.toDTO(trainee);
//            log.info("Returned trainee with username: {}", username);
//            return ResponseEntity.ok(traineeDTO);
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

//
//    @PutMapping("/activate/{id}")
//    public ResponseEntity<Void> activateTrainee(@PathVariable Long id,
//                                                @RequestParam boolean isActive,
//                                                @RequestHeader("username") String authUsername,
//                                                @RequestHeader("password") String authPassword) {
//        log.debug("Received request to activate/deactivate trainee with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                traineeService.activateTrainee(id, isActive);
//                log.info("Trainee with ID: {} has been {}", id, isActive ? "activated" : "deactivated");
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to activate/deactivate trainee with ID: {}", id, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
