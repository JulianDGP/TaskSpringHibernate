package com.example.CRMGym.SeviceTests;


import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.models.Training;
import com.example.CRMGym.repositories.TrainingRepository;
import com.example.CRMGym.services.implementations.TrainingServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainingServiceTests {

    @Mock
    private TrainingRepository trainingRepository;
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Esto inicializa tanto @Mock como @InjectMocks
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        Training createdTraining = trainingService.createTraining(training);
        assertNotNull(createdTraining);
        assertEquals(training, createdTraining);
    }

    @Test
    void testGetTraining() {
        Long id = 1L;
        Training expectedTraining = new Training();
        when(trainingRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(expectedTraining));

        Training training = trainingService.getTraining(id);
        assertNotNull(training);
        assertEquals(expectedTraining, training);
    }

    @Test
    void testUpdateTraining() {
        Long id = 1L;
        Training updatedTraining = new Training();
        when(trainingRepository.findById(id)).thenReturn(java.util.Optional.of(updatedTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(updatedTraining);

        Training result = trainingService.updateTraining(id, updatedTraining);

        assertNotNull(result);
        assertEquals(updatedTraining, result);
    }

    @Test
    void testDeleteTraining() {
        Long id = 1L;
        when(trainingRepository.findById(id)).thenReturn(java.util.Optional.of(new Training()));
        doNothing().when(trainingRepository).delete(any(Training.class));

        trainingService.deleteTraining(id);

        verify(trainingRepository, times(1)).delete(any(Training.class));
    }
}