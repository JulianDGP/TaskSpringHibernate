package com.example.CRMGym.repositories;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TrainingRepository  extends JpaRepository<Training, Long> {
    Set<Training> findByTraineeId (Long traineeId);
}
