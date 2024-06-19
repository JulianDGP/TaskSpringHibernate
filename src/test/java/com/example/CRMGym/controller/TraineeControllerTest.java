package com.example.CRMGym.controller;

import com.example.CRMGym.controllers.TraineeController;
import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.services.TraineeService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar filtros de seguridad para pruebas
public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeService traineeService;

    @MockBean
    private UserGenerationUtilities userGenerationUtilities;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterTrainee_Success() throws Exception {
        TraineeDTO traineeDTO = new TraineeDTO(null, "John", "Doe", null, LocalDate.of(1990, 1, 1), "123 Main St", true);
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);

        String username = "johndoe";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        when(userGenerationUtilities.generateUsername("John", "Doe")).thenReturn(username);
        when(userGenerationUtilities.generateRandomPassword()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(traineeService.createTrainee(any(Trainee.class))).thenReturn(trainee);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").value(password));

        verify(userGenerationUtilities, times(1)).generateUsername("John", "Doe");
        verify(userGenerationUtilities, times(1)).generateRandomPassword();
        verify(passwordEncoder, times(1)).encode(password);
        verify(traineeService, times(1)).createTrainee(any(Trainee.class));
    }

    @Test
    public void testRegisterTrainee_BadRequest() throws Exception {
        TraineeDTO traineeDTO = new TraineeDTO(null, "John", "Doe", null, LocalDate.of(1990, 1, 1), "123 Main St", true);

        when(userGenerationUtilities.generateUsername("John", "Doe")).thenThrow(new IllegalArgumentException("Error generating username"));

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Error generating username"))
                .andExpect(jsonPath("$.status").value(400));

        verify(userGenerationUtilities, times(1)).generateUsername("John", "Doe");
    }

    @Test
    public void testGetTraineeProfile_Success() throws Exception {
        String username = "testuser";
        TraineeProfileDTO traineeProfileDTO = new TraineeProfileDTO(1L, "John", "Doe", username, LocalDate.of(1990, 1, 1), "123 Main St", true, List.of());

        when(traineeService.getTraineeProfile(username)).thenReturn(traineeProfileDTO);

        mockMvc.perform(get("/api/trainees/profile/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        verify(traineeService, times(1)).getTraineeProfile(username);
    }

    @Test
    public void testGetTraineeProfile_NotFound() throws Exception {
        String username = "testuser";

        when(traineeService.getTraineeProfile(username)).thenThrow(new RuntimeException("Trainee not found"));

        mockMvc.perform(get("/api/trainees/profile/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).getTraineeProfile(username);
    }

    @Test
    public void testUpdateTraineeProfile_Success() throws Exception {
        String username = "testuser";
        TraineeDTO traineeDTO = new TraineeDTO(1L, "John", "Doe", username, LocalDate.of(1990, 1, 1), "123 Main St", true);
        TraineeProfileDTO traineeProfileDTO = new TraineeProfileDTO(1L, "John", "Doe", username, LocalDate.of(1990, 1, 1), "123 Main St", true, List.of());

        when(traineeService.updateTraineeProfile(anyString(), any(TraineeDTO.class))).thenReturn(traineeProfileDTO);

        mockMvc.perform(put("/api/trainees/profile/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        verify(traineeService, times(1)).updateTraineeProfile(anyString(), any(TraineeDTO.class));
    }

    @Test
    public void testUpdateTraineeProfile_NotFound() throws Exception {
        String username = "testuser";
        TraineeDTO traineeDTO = new TraineeDTO(1L, "John", "Doe", username, LocalDate.of(1990, 1, 1), "123 Main St", true);

        when(traineeService.updateTraineeProfile(anyString(), any(TraineeDTO.class))).thenThrow(new RuntimeException("Trainee not found"));

        mockMvc.perform(put("/api/trainees/profile/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).updateTraineeProfile(anyString(), any(TraineeDTO.class));
    }

    @Test
    public void testDeleteTraineeProfile_Success() throws Exception {
        String username = "testuser";

        doNothing().when(traineeService).deleteTraineeByUsername(username);

        mockMvc.perform(delete("/api/trainees/profile/{username}", username))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).deleteTraineeByUsername(username);
    }

    @Test
    public void testDeleteTraineeProfile_NotFound() throws Exception {
        String username = "testuser";

        doThrow(new RuntimeException("Trainee not found")).when(traineeService).deleteTraineeByUsername(username);

        mockMvc.perform(delete("/api/trainees/profile/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).deleteTraineeByUsername(username);
    }

    @Test
    public void testGetNotAssignedActiveTrainers_Success() throws Exception {
        String username = "testuser";
        List<TrainerDTO> trainers = List.of(new TrainerDTO(1L, "Trainer", "One", "trainerone", true, "Yoga"));

        when(traineeService.getNotAssignedActiveTrainers(username)).thenReturn(trainers);

        mockMvc.perform(get("/api/trainees/not-assigned/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainerone"));

        verify(traineeService, times(1)).getNotAssignedActiveTrainers(username);
    }

    @Test
    public void testGetNotAssignedActiveTrainers_NotFound() throws Exception {
        String username = "testuser";

        when(traineeService.getNotAssignedActiveTrainers(username)).thenThrow(new RuntimeException("Trainee not found"));

        mockMvc.perform(get("/api/trainees/not-assigned/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).getNotAssignedActiveTrainers(username);
    }

    @Test
    public void testGetTraineeTrainings_Success() throws Exception {
        String username = "testuser";
        List<TrainingDTO> trainings = List.of(new TrainingDTO(1L, 1L, 1L, "Training 1", "CARDIO", LocalDateTime.now(), 60));

        when(traineeService.getTraineeTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString()))
                .thenReturn(trainings);

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", username)
                        .param("fromDate", "2023-01-01T00:00:00")
                        .param("toDate", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Training 1"))
                .andExpect(jsonPath("$[0].trainingType").value("CARDIO"));

        verify(traineeService, times(1)).getTraineeTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString());
    }

    @Test
    public void testGetTraineeTrainings_NotFound() throws Exception {
        String username = "testuser";

        when(traineeService.getTraineeTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString()))
                .thenThrow(new RuntimeException("Trainee or Trainings not found"));

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", username)
                        .param("fromDate", "2023-01-01T00:00:00")
                        .param("toDate", "2023-12-31T23:59:59"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee or Trainings not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).getTraineeTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString());
    }

    @Test
    public void testUpdateTraineeActiveStatus_Success() throws Exception {
        Map<String, Object> payload = Map.of("username", "testuser", "isActive", true);
        String jsonRequest = objectMapper.writeValueAsString(payload);

        doNothing().when(traineeService).updateTraineeActiveStatus(anyString(), anyBoolean());

        mockMvc.perform(patch("/api/trainees/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(traineeService, times(1)).updateTraineeActiveStatus(anyString(), anyBoolean());
    }

    @Test
    public void testUpdateTraineeActiveStatus_NotFound() throws Exception {
        Map<String, Object> payload = Map.of("username", "testuser", "isActive", true);
        String jsonRequest = objectMapper.writeValueAsString(payload);

        doThrow(new RuntimeException("Trainee not found")).when(traineeService).updateTraineeActiveStatus(anyString(), anyBoolean());

        mockMvc.perform(patch("/api/trainees/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(traineeService, times(1)).updateTraineeActiveStatus(anyString(), anyBoolean());
    }
}
