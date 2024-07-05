package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.LoginAttempt;
import com.example.CRMGym.repositories.LoginAttemptRepository;
import com.example.CRMGym.services.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION_MINUTES = 5; // 5 minutes
    private final LoginAttemptRepository loginAttemptRepository;

    @Autowired
    public LoginAttemptServiceImpl(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public void loginFailed(String username) {
        // Crear un nuevo intento de login y guardarlo en la base de datos
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUsername(username);
        loginAttempt.setAttemptTime(LocalDateTime.now());
        loginAttemptRepository.save(loginAttempt);
    }

    @Override
    public boolean isBlocked(String username) {
        // Obtener los intentos de login fallidos en los últimos 5 minutos
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(LOCK_TIME_DURATION_MINUTES);
        List<LoginAttempt> recentAttempts = loginAttemptRepository.findAttemptsByUsernameSince(username, fiveMinutesAgo);

        // Si hay 3 o más intentos fallidos en los últimos 5 minutos, el usuario está bloqueado
        return recentAttempts.size() >= MAX_ATTEMPTS;
    }

}
