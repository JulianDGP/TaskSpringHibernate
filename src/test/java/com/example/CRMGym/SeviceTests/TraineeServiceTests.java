package com.example.CRMGym.SeviceTests;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.repositories.TraineeRepository;
import com.example.CRMGym.services.implementations.TraineeServiceImpl;
import com.example.CRMGym.utilities.UserGenerationUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

public class TraineeServiceTests {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserGenerationUtilities userGenerationUtilities;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainees() {
        when(traineeRepository.findAll()).thenReturn(List.of(new Trainee()));

        List<Trainee> trainees = traineeService.getAllTrainees();
        assertNotNull(trainees);
        assertEquals(1, trainees.size());
    }

    @Test
    void testGetTraineeById() {
        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.of(expectedTrainee));

        Trainee trainee = traineeService.getTrainee(1L);
        assertNotNull(trainee);
        assertEquals(expectedTrainee, trainee);
    }

    @Test
    void testGetTraineeByUsername() {
        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.findByUsername(anyString())).thenReturn(Optional.of(expectedTrainee));

        Trainee trainee = traineeService.getTraineeByUsername("username");
        assertNotNull(trainee);
        assertEquals(expectedTrainee, trainee);
    }

    @Test
    void testCreateTrainee() {
        Trainee traineeToSave = new Trainee();
        traineeToSave.setFirstName("John");
        traineeToSave.setLastName("Doe");

        when(userGenerationUtilities.generateUsername(anyString(), anyString())).thenReturn("username");
        when(userGenerationUtilities.generateRandomPassword()).thenReturn("password");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee savedTrainee = traineeService.createTrainee(traineeToSave);
        assertNotNull(savedTrainee);
        assertEquals("username", savedTrainee.getUsername());
        assertEquals("encodedPassword", savedTrainee.getPassword());
    }

    @Test
    void testUpdateTrainee() {
        Trainee existingTrainee = new Trainee();
        Trainee traineeToUpdate = new Trainee();
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.of(existingTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(traineeToUpdate);

        Trainee updatedTrainee = traineeService.updateTrainee(1L, traineeToUpdate);
        assertNotNull(updatedTrainee);
        assertEquals(traineeToUpdate, updatedTrainee);
    }

    @Test
    void testChangeTraineePassword() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.of(trainee));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        traineeService.changeTraineePassword(1L, "newPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testDeleteTraineeByUsername() {
        Trainee traineeToDelete = new Trainee();
        when(traineeRepository.findByUsername(anyString())).thenReturn(Optional.of(traineeToDelete));

        traineeService.deleteTraineeByUsername("username");
        verify(traineeRepository, times(1)).delete(traineeToDelete);
    }

    @Test
    void testActivateTrainee() {
        Trainee traineeToActivate = new Trainee();
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.of(traineeToActivate));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(traineeToActivate);

        traineeService.activateTrainee(1L, true);
        assertTrue(traineeToActivate.isActive());
        verify(traineeRepository, times(1)).save(traineeToActivate);
    }
}