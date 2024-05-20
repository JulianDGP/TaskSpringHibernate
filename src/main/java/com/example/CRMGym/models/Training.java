package com.example.CRMGym.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    @NotNull(message = "Trainee must be specified for the training.")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    @NotNull(message = "Trainer must be specified for the training.")
    private Trainer trainer;

    @NotBlank(message = "Training name is required.")
    @Column(nullable = false)
    private String trainingName;
    @Enumerated(EnumType.STRING)  // Guarda el nombre del Enum como String en la DB.
    @NotNull(message = "Training type is required.")
    @Column(nullable = false)
    private TrainingType trainingType;
    @NotNull(message = "Training date is required.")
    @Column(nullable = false)
    private LocalDateTime trainingDate;

    @NotNull(message = "Training duration is required.")
    @Column(nullable = false)
    private int trainingDuration;

    public Training() {
    }

    public Training(Long id, Trainee trainee, Trainer trainer, String trainingName, TrainingType trainingType, LocalDateTime trainingDate, int trainingDuration) {
        this.id = id;
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDateTime trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
