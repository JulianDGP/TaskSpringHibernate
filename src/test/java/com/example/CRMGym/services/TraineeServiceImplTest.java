package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TraineeDTO;
import com.example.CRMGym.models.dto.TraineeProfileDTO;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.implementations.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    private Training training;

    @BeforeEach
    public void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("testuser");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setActive(true);

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(new Trainer());
        training.setTrainingName("Cardio Training");
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(60);
        training.setTrainingType(TrainingType.CARDIO);
    }

    @Test
    @Transactional
    public void testCreateTrainee() {
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee createdTrainee = traineeService.createTrainee(trainee);

        assertNotNull(createdTrainee);
        assertEquals(trainee.getId(), createdTrainee.getId());
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTrainee() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        Trainee foundTrainee = traineeService.getTrainee(1L);

        assertNotNull(foundTrainee);
        assertEquals(trainee.getId(), foundTrainee.getId());
        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTraineeByUsername() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));

        Trainee foundTrainee = traineeService.getTraineeByUsername("testuser");

        assertNotNull(foundTrainee);
        assertEquals(trainee.getUsername(), foundTrainee.getUsername());
        verify(traineeRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTraineeProfile() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));
        when(trainingRepository.findByTraineeId(1L)).thenReturn(Set.of());

        TraineeProfileDTO profile = traineeService.getTraineeProfile("testuser");

        assertNotNull(profile);
        assertEquals(trainee.getUsername(), profile.username());
        verify(traineeRepository, times(1)).findByUsername("testuser");
        verify(trainingRepository, times(1)).findByTraineeId(1L);
    }

    @Test
    @Transactional
    public void testUpdateTraineeProfile() {
        TraineeDTO traineeDTO = new TraineeDTO(1L, "John", "Doe", "testuser", LocalDate.of(1990, 1, 1), "123 Main St", true);
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(trainingRepository.findByTraineeId(1L)).thenReturn(Set.of());

        TraineeProfileDTO updatedProfile = traineeService.updateTraineeProfile("testuser", traineeDTO);

        assertNotNull(updatedProfile);
        assertEquals(trainee.getUsername(), updatedProfile.username());
        verify(traineeRepository, times(1)).findByUsername("testuser");
        verify(traineeRepository, times(1)).save(any(Trainee.class));
        verify(trainingRepository, times(1)).findByTraineeId(1L);
    }

    @Test
    @Transactional
    public void testDeleteTraineeByUsername() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername("testuser");

        verify(traineeRepository, times(1)).findByUsername("testuser");
        verify(traineeRepository, times(1)).delete(trainee);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetNotAssignedActiveTrainers() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByIsActive(true)).thenReturn(List.of(new Trainer()));
        when(trainerRepository.findTrainersByTraineeId(1L)).thenReturn(List.of());

        List<TrainerDTO> trainers = traineeService.getNotAssignedActiveTrainers("testuser");

        assertNotNull(trainers);
        verify(traineeRepository, times(1)).findByUsername("testuser");
        verify(trainerRepository, times(1)).findAllByIsActive(true);
        verify(trainerRepository, times(1)).findTrainersByTraineeId(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTraineeTrainings() {
        when(trainingRepository.findTrainingsByFilters(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString()))
                .thenReturn(List.of(training));

        List<TrainingDTO> trainings = traineeService.getTraineeTrainings("testuser", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "trainer", "Cardio");

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        verify(trainingRepository, times(1)).findTrainingsByFilters(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyString());
    }

    @Test
    @Transactional
    public void testUpdateTraineeActiveStatus() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        traineeService.updateTraineeActiveStatus("testuser", false);

        assertFalse(trainee.isActive());
        verify(traineeRepository, times(1)).findByUsername("testuser");
        verify(traineeRepository, times(1)).save(trainee);
    }

}
