package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.TrainingType;
import com.example.CRMGym.models.dto.TrainerDTO;
import com.example.CRMGym.models.dto.TrainerProfileDTO;
import com.example.CRMGym.models.dto.TrainingDTO;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private Training training;

    @BeforeEach
    public void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("testtrainer");

        training = new Training();
        training.setId(1L);
        training.setTrainee(new Trainee());
        training.setTrainer(trainer);
        training.setTrainingName("Cardio Training");
        training.setTrainingDate(LocalDateTime.now());
        training.setTrainingDuration(60);
        training.setTrainingType(TrainingType.CARDIO);
    }

    @Test
    @Transactional
    public void testCreateTrainer() {
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer createdTrainer = trainerService.createTrainer(trainer);

        assertNotNull(createdTrainer);
        assertEquals(trainer.getId(), createdTrainer.getId());
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTrainer() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.getTrainer(1L);

        assertNotNull(foundTrainer);
        assertEquals(trainer.getId(), foundTrainer.getId());
        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTrainerByUsername() {
        when(trainerRepository.findByUsername("testtrainer")).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.getTrainerByUsername("testtrainer");

        assertNotNull(foundTrainer);
        assertEquals(trainer.getUsername(), foundTrainer.getUsername());
        verify(trainerRepository, times(1)).findByUsername("testtrainer");
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTrainerProfile() {
        when(trainerRepository.findByUsername("testtrainer")).thenReturn(Optional.of(trainer));
        when(trainingRepository.findByTrainerId(1L)).thenReturn(Set.of());

        TrainerProfileDTO profile = trainerService.getTrainerProfile("testtrainer");

        assertNotNull(profile);
        assertEquals(trainer.getUsername(), profile.username());
        verify(trainerRepository, times(1)).findByUsername("testtrainer");
        verify(trainingRepository, times(1)).findByTrainerId(1L);
    }

    @Test
    @Transactional
    public void testUpdateTrainerProfile() {
        TrainerDTO trainerDTO = new TrainerDTO(1L, "Jane", "Doe", "testtrainer", true, "Cardio");
        when(trainerRepository.findByUsername("testtrainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingRepository.findByTrainerId(1L)).thenReturn(Set.of());

        TrainerProfileDTO updatedProfile = trainerService.updateTrainerProfile("testtrainer", trainerDTO);

        assertNotNull(updatedProfile);
        assertEquals(trainer.getUsername(), updatedProfile.username());
        verify(trainerRepository, times(1)).findByUsername("testtrainer");
        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(trainingRepository, times(1)).findByTrainerId(1L);
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTrainerTrainings() {
        when(trainingRepository.findTrainingsByTrainerFilters(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(List.of(training));

        List<TrainingDTO> trainings = trainerService.getTrainerTrainings("testtrainer", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "trainee");

        assertNotNull(trainings);
        verify(trainingRepository, times(1)).findTrainingsByTrainerFilters(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    @Test
    @Transactional
    public void testUpdateTrainerActiveStatus() {
        when(trainerRepository.findByUsername("testtrainer")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        trainerService.updateTrainerActiveStatus("testtrainer", false);

        assertFalse(trainer.isActive());
        verify(trainerRepository, times(1)).findByUsername("testtrainer");
        verify(trainerRepository, times(1)).save(trainer);
    }

}
