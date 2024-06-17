package com.example.CRMGym.repositories;

import com.example.CRMGym.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    @Query("SELECT t FROM Trainer t WHERE t.isActive = :isActive")
    List<Trainer> findAllByIsActive(@Param("isActive") boolean isActive);

    @Query("SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.id = :traineeId")
    List<Trainer> findTrainersByTraineeId(@Param("traineeId") Long traineeId);

}
