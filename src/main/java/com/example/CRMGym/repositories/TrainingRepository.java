package com.example.CRMGym.repositories;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TrainingRepository  extends JpaRepository<Training, Long> {
    Set<Training> findByTraineeId (Long traineeId);
    Set<Training> findByTrainerId (Long trainerId);

    @Query("SELECT t FROM Training t WHERE t.trainee.username = :username " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:trainerName IS NULL OR t.trainer.username = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType = :trainingType)")
    List<Training> findTrainingsByFilters(@Param("username") String username,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate,
                                          @Param("trainerName") String trainerName,
                                          @Param("trainingType") String trainingType);
    @Query("SELECT t FROM Training t WHERE t.trainer.username = :username " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:traineeName IS NULL OR t.trainee.username = :traineeName)")
    List<Training> findTrainingsByTrainerFilters(@Param("username") String username,
                                                 @Param("fromDate") LocalDateTime fromDate,
                                                 @Param("toDate") LocalDateTime toDate,
                                                 @Param("traineeName") String traineeName);
}
