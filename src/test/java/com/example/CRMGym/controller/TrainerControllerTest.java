package com.example.CRMGym.controller;


import com.example.CRMGym.controllers.TrainerController;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.services.TrainerService;
import com.example.CRMGym.utilities.UserGenerationUtilities;
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
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar filtros de seguridad para pruebas
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private UserGenerationUtilities userGenerationUtilities;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterTrainer_Success() throws Exception {
        TrainerDTO trainerDTO = new TrainerDTO(null, "John", "Doe", null, true, "Fitness");
        Trainer trainer = new Trainer(1L, "John", "Doe", "john.doe", "encodedPassword", true, "Fitness");
        String generatedPassword = "randomPassword";

        when(userGenerationUtilities.generateUsername(anyString(), anyString())).thenReturn("john.doe");
        when(userGenerationUtilities.generateRandomPassword()).thenReturn(generatedPassword);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(trainerService.createTrainer(any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\", \"specialization\":\"Fitness\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value(generatedPassword));

        verify(trainerService, times(1)).createTrainer(any(Trainer.class));
    }

    @Test
    public void testGetTrainerProfile_Success() throws Exception {
        String username = "john.doe";
        TrainerProfileDTO trainerProfileDTO = new TrainerProfileDTO(1L, "John", "Doe", "john.doe", true, "Fitness", List.of());

        when(trainerService.getTrainerProfile(username)).thenReturn(trainerProfileDTO);

        mockMvc.perform(get("/api/trainers/profile/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.specialization").value("Fitness"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(trainerService, times(1)).getTrainerProfile(username);
    }

    @Test
    public void testUpdateTrainerProfile_Success() throws Exception {
        String username = "john.doe";
        TrainerDTO trainerDTO = new TrainerDTO(1L, "John", "Doe", username, true, "Fitness");
        TrainerProfileDTO updatedTrainerProfileDTO = new TrainerProfileDTO(1L, "John", "Doe", username, true, "Fitness", List.of());

        when(trainerService.updateTrainerProfile(anyString(), any(TrainerDTO.class))).thenReturn(updatedTrainerProfileDTO);

        mockMvc.perform(put("/api/trainers/profile/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"firstName\":\"John\", \"lastName\":\"Doe\", \"username\":\"john.doe\", \"specialization\":\"Fitness\", \"isActive\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.specialization").value("Fitness"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(trainerService, times(1)).updateTrainerProfile(anyString(), any(TrainerDTO.class));
    }

    @Test
    public void testGetTrainerTrainings_Success() throws Exception {
        String username = "john.doe";
        List<TrainingDTO> trainings = List.of(new TrainingDTO(1L, 1L, 1L, "Training 1", "CARDIO", LocalDateTime.now(), 60));

        when(trainerService.getTrainerTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString())).thenReturn(trainings);

        mockMvc.perform(get("/api/trainers/trainings")
                        .param("username", username)
                        .param("fromDate", "2023-01-01T00:00:00")
                        .param("toDate", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Training 1"))
                .andExpect(jsonPath("$[0].trainingType").value("CARDIO"));

        verify(trainerService, times(1)).getTrainerTrainings(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    @Test
    public void testUpdateTrainerActiveStatus_Success() throws Exception {
        String username = "john.doe";
        boolean isActive = true;

        doNothing().when(trainerService).updateTrainerActiveStatus(username, isActive);

        mockMvc.perform(patch("/api/trainers/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john.doe\", \"isActive\":true}"))
                .andExpect(status().isOk());

        verify(trainerService, times(1)).updateTrainerActiveStatus(username, isActive);
    }
}