package com.example.CRMGym.utilities;

import com.example.CRMGym.repositories.UserRepository;
import com.example.CRMGym.services.implementations.TrainerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserGenerationUtilities {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final int PASSWORD_LENGTH = 10;
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final UserRepository userRepository;  // UserRepository para manejar ambos, Trainer y Trainee


    @Autowired
    public UserGenerationUtilities(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateRandomPassword() {
        log.debug("Generating a random password.");
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        log.debug("Generated password");
        return sb.toString();
    }

    public String generateUsername(String firstName, String lastName) {
        log.debug("Generating username for firstName: {} and lastName: {}", firstName, lastName);
        String baseUsername = firstName.trim() + "." + lastName.trim();
        String username = baseUsername;
        int count = 0;
        while (usernameExists(username)) {
            count++;
            username = baseUsername + count;
            log.debug("Username already exists. Trying new username: {}", username);
        }
        log.info("Generated username: {}", username);
        return username;
    }

    private boolean usernameExists(String username) {
        log.debug("Checking if username exists: {}", username);
        boolean exists = userRepository.findByUsername(username).isPresent();
        log.debug("Username {} exists: {}", username, exists);
        return exists;
    }
}

