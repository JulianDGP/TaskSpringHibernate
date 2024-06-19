package com.example.CRMGym.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.CRMGym.controllers.TrainingController;

import com.example.CRMGym.mappers.TrainingMapper;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.services.TrainingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;


@WebMvcTest(TrainingController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar filtros de seguridad para pruebas
public class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTraining_Success() throws Exception {
        Long trainingId = 1L;
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", "password", true, LocalDate.now(), "123 Main St");
        Trainer trainer = new Trainer(1L, "Jane", "Smith", "jane.smith", "password", true, "Strength");
        TrainingType trainingType = TrainingType.CARDIO;
        Training training = new Training(trainingId, trainee, trainer, "Training 1", trainingType, LocalDateTime.now(), 60);
        TrainingDTO trainingDTO = TrainingMapper.toDTO(training);

        when(trainingService.getTraining(trainingId)).thenReturn(training);

        mockMvc.perform(get("/api/trainings/{id}", trainingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingName").value("Training 1"))
                .andExpect(jsonPath("$.trainingType").value("CARDIO"));

        verify(trainingService, times(1)).getTraining(trainingId);
    }

    @Test
    public void testGetTraining_NotFound() throws Exception {
        Long trainingId = 1L;

        when(trainingService.getTraining(trainingId)).thenThrow(new RuntimeException("Training not found with ID: " + trainingId));

        mockMvc.perform(get("/api/trainings/{id}", trainingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found with ID: " + trainingId))
                .andExpect(jsonPath("$.status").value(404));

        verify(trainingService, times(1)).getTraining(trainingId);
    }

    @Test
    public void testCreateTraining_Success() throws Exception {
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", "password", true, LocalDate.now(), "123 Main St");
        Trainer trainer = new Trainer(1L, "Jane", "Smith", "jane.smith", "password", true, "Strength");
        TrainingType trainingType = TrainingType.CARDIO;
        Training training = new Training(1L, trainee, trainer, "Training 1", trainingType, LocalDateTime.now(), 60);
        TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO("john.doe", "jane.smith", "Training 1", LocalDateTime.now(), 60);
        TrainingDTO trainingDTO = TrainingMapper.toDTO(training);

        when(trainingService.createTraining(any(TrainingRequestDTO.class))).thenReturn(training);

        mockMvc.perform(post("/api/trainings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"john.doe\",\"trainerUsername\":\"jane.smith\",\"trainingName\":\"Training 1\",\"trainingDate\":\"2023-06-19T10:15:30\",\"trainingDuration\":60}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainingName").value("Training 1"))
                .andExpect(jsonPath("$.trainingType").value("CARDIO"));

        verify(trainingService, times(1)).createTraining(any(TrainingRequestDTO.class));
    }

    @Test
    public void testCreateTraining_BadRequest() throws Exception {
        when(trainingService.createTraining(any(TrainingRequestDTO.class))).thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(post("/api/trainings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"traineeUsername\":\"john.doe\",\"trainerUsername\":\"jane.smith\",\"trainingName\":\"Training 1\",\"trainingDate\":\"2023-06-19T10:15:30\",\"trainingDuration\":60}"))
                .andExpect(status().isBadRequest());

        verify(trainingService, times(1)).createTraining(any(TrainingRequestDTO.class));
    }

    @Test
    public void testGetAllTrainingTypes_Success() throws Exception {
        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingType").value("CARDIO"))
                .andExpect(jsonPath("$[0].displayName").value("Cardio"))
                .andExpect(jsonPath("$[1].trainingType").value("STRENGTH"))
                .andExpect(jsonPath("$[1].displayName").value("Strength"));
    }
}