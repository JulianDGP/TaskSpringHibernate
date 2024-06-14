package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TraineeMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* 1.Trainee Registration with POST method */
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


    @GetMapping("/{id}")
    public ResponseEntity<TraineeDTO> getTrainee(@PathVariable Long id,
                                                 @RequestHeader("username") String authUsername,
                                                 @RequestHeader("password") String authPassword) {
        log.debug("Received request to get trainee by ID: {}", id);
        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
            Trainee trainee = traineeService.getTrainee(id);
            if (trainee == null) {
                log.warn("No trainee found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            TraineeDTO traineeDTO = TraineeMapper.toDTO(trainee);
            log.info("Returned trainee with ID: {}", id);
            return ResponseEntity.ok(traineeDTO);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getTraineeProfile(@PathVariable String username) {
        try {
            TraineeProfileDTO traineeProfileDTO = traineeService.getTraineeProfile(username);
            return ResponseEntity.ok(traineeProfileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Trainee not found", HttpStatus.NOT_FOUND.value()));
        }
    }
}

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


//    @PutMapping("/{id}")
//    public ResponseEntity<TraineeDTO> updateTrainee(@PathVariable Long id,
//                                                    @RequestBody TraineeDTO traineeDTO,
//                                                    @RequestHeader("username") String authUsername,
//                                                    @RequestHeader("password") String authPassword) {
//        log.debug("Received request to update trainee with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            Trainee trainee = TraineeMapper.toEntity(traineeDTO);
//            trainee.setId(id);
//            Trainee updatedTrainee = traineeService.updateTrainee(id, trainee);
//            if (updatedTrainee == null) {
//                log.error("Failed to find trainee with ID: {} for update", id);
//                return ResponseEntity.notFound().build();
//            }
//            log.info("Successfully updated trainee with ID: {}", id);
//            TraineeDTO updatedTraineeDTO = TraineeMapper.toDTO(updatedTrainee);
//            return ResponseEntity.ok(updatedTraineeDTO);
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }

//    @PutMapping("/{id}/change-password")
//    public ResponseEntity<Void> changeTraineePassword(@PathVariable Long id,
//                                                      @RequestBody Map<String, String> request,
//                                                      @RequestHeader("username") String authUsername,
//                                                      @RequestHeader("password") String authPassword) {
//        log.debug("Received request to change password for trainee with ID: {}", id);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                String newPassword = request.get("newPassword");
//                traineeService.changeTraineePassword(id, newPassword);
//                log.info("Trainee with ID: {} has changed their password", id);
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to change password for trainee with ID: {}", id, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
//
//    @DeleteMapping("/{username}")
//    public ResponseEntity<Void> deleteTraineeByUsername(@PathVariable String username,
//                                                        @RequestHeader("username") String authUsername,
//                                                        @RequestHeader("password") String authPassword) {
//        log.debug("Received request to delete trainee with username: {}", username);
//        if (userGenerationUtilities.checkCredentials(authUsername, authPassword)) {
//            try {
//                traineeService.deleteTraineeByUsername(username);
//                log.info("Successfully deleted trainee with username: {}", username);
//                return ResponseEntity.ok().build();
//            } catch (Exception e) {
//                log.error("Failed to delete trainee with username: {}", username, e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
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
