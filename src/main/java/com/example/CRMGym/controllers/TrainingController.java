package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.mappers.TrainingMapper;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.services.TrainingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Training", description = "Get training by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getTraining(@PathVariable Long id) {
        log.debug("Received request to get training by ID: {}", id);
        try {
            Training training = trainingService.getTraining(id);
            TrainingDTO trainingDTO = TrainingMapper.toDTO(training);
            log.info("Returned training with ID: {}", id);
            return ResponseEntity.ok(trainingDTO);
        } catch (RuntimeException e) {
            log.warn("No training found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Training not found with ID: " + id, HttpStatus.NOT_FOUND.value()));
        }
    }

    /* 14. Add Training with Post Method */
    @Operation(summary = "Add Training", description = "Create a new training", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/create")
    public ResponseEntity<?> createTraining(@RequestBody TrainingRequestDTO trainingRequestDTO) {
        log.debug("Received request to create a new training");
        try {
            Training createdTraining = trainingService.createTraining(trainingRequestDTO);
            TrainingDTO createdTrainingDTO = TrainingMapper.toDTO(createdTraining);
            log.info("Training created successfully: {}", createdTrainingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainingDTO);
        } catch (IllegalArgumentException e) {
            log.error("Error creating training: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating training: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /* 17. Get Training types with GET method */
    @Operation(summary = "Get Training Types", description = "Get all training types", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getAllTrainingTypes() {
        log.info("Received request to get all training types");
        List<Map<String, String>> trainingTypes = Arrays.stream(TrainingType.values())
                .map(trainingType -> Map.of(
                        "trainingType", trainingType.name(),
                        "displayName", trainingType.getDisplayName()
                ))
                .collect(Collectors.toList());
        log.info("Returned all training types");
        return ResponseEntity.ok(trainingTypes);
    }

}
