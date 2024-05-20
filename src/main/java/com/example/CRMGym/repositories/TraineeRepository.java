package com.example.CRMGym.repositories;

import com.example.CRMGym.models.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
}
