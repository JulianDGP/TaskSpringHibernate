package com.example.CRMGym.SeviceTests;


import com.example.CRMGym.models.Trainer;
import com.example.CRMGym.repositories.TrainerRepository;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class TrainerServiceTests {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserGenerationUtilities userGenerationUtilities;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainers() {
        when(trainerRepository.findAll()).thenReturn(List.of(new Trainer()));
        List<Trainer> trainers = trainerService.getAllTrainers();
        assertNotNull(trainers);
        assertEquals(1, trainers.size());
    }

    @Test
    void testGetTrainerById() {
        Trainer expectedTrainer = new Trainer();
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(expectedTrainer));
        Trainer trainer = trainerService.getTrainer(1L);
        assertNotNull(trainer);
        assertEquals(expectedTrainer, trainer);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainerToSave = new Trainer();
        trainerToSave.setFirstName("John");
        trainerToSave.setLastName("Doe");

        when(userGenerationUtilities.generateUsername(anyString(), anyString())).thenReturn("username");
        when(userGenerationUtilities.generateRandomPassword()).thenReturn("password");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Configura el mock para devolver el mismo objeto que recibe
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer savedTrainer = trainerService.createTrainer(trainerToSave);
        assertNotNull(savedTrainer);
        assertEquals("username", savedTrainer.getUsername());
        assertEquals("encodedPassword", savedTrainer.getPassword());
    }

    @Test
    void testUpdateTrainer() {
        Trainer existingTrainer = new Trainer();
        Trainer trainerToUpdate = new Trainer();
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerToUpdate);

        Trainer updatedTrainer = trainerService.updateTrainer(1L, trainerToUpdate);
        assertNotNull(updatedTrainer);
        assertEquals(trainerToUpdate, updatedTrainer);
    }

    @Test
    void testChangeTrainerPassword() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainer));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        trainerService.changeTrainerPassword(1L, "newPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    void testDeleteTrainerByUsername() {
        Trainer trainerToDelete = new Trainer();
        when(trainerRepository.findByUsername(anyString())).thenReturn(Optional.of(trainerToDelete));

        trainerService.deleteTrainerByUsername("username");
        verify(trainerRepository, times(1)).delete(trainerToDelete);
    }

    @Test
    void testActivateTrainer() {
        Trainer trainerToActivate = new Trainer();
        when(trainerRepository.findById(anyLong())).thenReturn(Optional.of(trainerToActivate));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerToActivate);

        trainerService.activateTrainer(1L, true);
        assertTrue(trainerToActivate.isActive());
        verify(trainerRepository, times(1)).save(trainerToActivate);
    }
}