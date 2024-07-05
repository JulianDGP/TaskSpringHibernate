package com.example.CRMGym.repositories;

import com.example.CRMGym.models.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByUsername(String username);

    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.attemptTime >= :since ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findAttemptsByUsernameSince(@Param("username") String username, @Param("since") LocalDateTime since);
}
