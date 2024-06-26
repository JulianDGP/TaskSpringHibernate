package com.example.CRMGym.services.implementations;

import com.example.CRMGym.models.User;
import com.example.CRMGym.repositories.UserRepository;
import com.example.CRMGym.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        // Encode new password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        // Update user's password
        user.setPassword(encodedNewPassword);
        userRepository.save(user); // Save updated user
        log.info("Password changed successfully for user: {}", username);
    }
}
