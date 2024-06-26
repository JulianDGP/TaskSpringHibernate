package com.example.CRMGym.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
@Component
public class CustomMetrics {
    private final MeterRegistry meterRegistry;

    @Autowired
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    @PostConstruct
    public void init() {
        // Registrar un temporizador personalizado para el registro de trainees
        Timer.builder("crm_gym.trainee.registration.time")
                .description("Time taken to register a new trainee")
                .register(meterRegistry);

        // Registrar un temporizador personalizado para el registro de trainers
        Timer.builder("crm_gym.trainer.registration.time")
                .description("Time taken to register a new trainer")
                .register(meterRegistry);
    }

    public void recordTraineeRegistrationTime(long durationInMillis) {
        meterRegistry.timer("crm_gym.trainee.registration.time").record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void recordTrainerRegistrationTime(long durationInMillis) {
        meterRegistry.timer("crm_gym.trainer.registration.time").record(durationInMillis, TimeUnit.MILLISECONDS);
    }
}
