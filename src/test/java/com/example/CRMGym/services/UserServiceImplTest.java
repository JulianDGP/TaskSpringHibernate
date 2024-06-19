package com.example.CRMGym.services;

import com.example.CRMGym.models.Trainee;
import com.example.CRMGym.repositories.UserRepository;
import com.example.CRMGym.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testChangePassword_Success() {
        String username = "testuser";
        String oldPassword = "oldpass";
        String newPassword = "newpass";
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setPassword(oldPassword);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");

        userService.changePassword(username, oldPassword, newPassword);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(trainee);

        assertEquals("encodedNewPass", trainee.getPassword());
    }

    @Test
    public void testChangePassword_InvalidOldPassword() {
        String username = "testuser";
        String oldPassword = "oldpass";
        String newPassword = "newpass";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(username, oldPassword, newPassword);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(Trainee.class));
    }
}
