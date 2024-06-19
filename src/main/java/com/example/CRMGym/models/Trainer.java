package com.example.CRMGym.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer extends User {

    @NotBlank(message = "Specialization is required.")
    @Column(nullable = true)
    private String specialization;
    // Relationships with cascade delete
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

    public Trainer() {
    }
    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean isActive, String specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Set<Training> trainings) {
        this.trainings = trainings;
    }
}
