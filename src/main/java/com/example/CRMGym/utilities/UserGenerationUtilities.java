package com.example.CRMGym.utilities;

import com.example.CRMGym.models.User;
import com.example.CRMGym.repositories.UserRepository;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserGenerationUtilities {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final int PASSWORD_LENGTH = 10;
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Autowired
    private final UserRepository userRepository;  // UserRepository para manejar ambos, Trainer y Trainee

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserGenerationUtilities(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        return sb.toString();
    }

    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int count = 0;
        while (usernameExists(username)) {
            count++;
            username = baseUsername + count;
        }
        return username;
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean checkCredentials(String username, String rawPassword) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            //log.debug("Checking credentials for user: {} with raw password: {}", username, rawPassword);
            boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
            log.debug("Password match result: {}", passwordMatches);

            return passwordMatches;
        } catch (Exception e) {
            log.error("Authentication failed for username: {}", username, e);
            return false;
        }
    }


}
