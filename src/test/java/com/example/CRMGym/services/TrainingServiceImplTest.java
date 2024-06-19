package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.models.dto.TrainingRequestDTO;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.implementations.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)

public class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingRequestDTO trainingRequestDTO;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("traineeUser");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("trainerUser");

        trainingRequestDTO = new TrainingRequestDTO(
                "traineeUser",
                "trainerUser",
                "Cardio Training",
                LocalDateTime.now(),
                60
        );
    }

    @Test
    @Transactional
    public void testCreateTraining() {
        when(traineeRepository.findByUsername("traineeUser")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainerUser")).thenReturn(Optional.of(trainer));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingRequestDTO.trainingName());
        training.setTrainingDate(trainingRequestDTO.trainingDate());
        training.setTrainingDuration(trainingRequestDTO.trainingDuration());

        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Training createdTraining = trainingService.createTraining(trainingRequestDTO);

        assertNotNull(createdTraining);
        assertEquals(training.getTrainingName(), createdTraining.getTrainingName());
        verify(traineeRepository, times(1)).findByUsername("traineeUser");
        verify(trainerRepository, times(1)).findByUsername("trainerUser");
        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetTraining() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Cardio Training");

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training foundTraining = trainingService.getTraining(1L);

        assertNotNull(foundTraining);
        assertEquals(training.getId(), foundTraining.getId());
        verify(trainingRepository, times(1)).findById(1L);
    }
}
