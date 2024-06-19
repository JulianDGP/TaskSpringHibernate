package com.example.CRMGym.controller;


import com.example.CRMGym.controllers.AuthController;
import com.example.CRMGym.security.JwtTokenProvider;
import com.example.CRMGym.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilitar filtros de seguridad para pruebas
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() throws Exception {
        String username = "testuser";
        String password = "testpass";
        String token = "dummytoken";

        Map<String, String> credentials = Map.of("username", username, "password", password);
        String jsonRequest = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.createToken(username)).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).createToken(username);
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        String username = "testuser";
        String password = "wrongpass";

        Map<String, String> credentials = Map.of("username", username, "password", password);
        String jsonRequest = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials."))
                .andExpect(jsonPath("$.status").value(401));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

    @Test
    public void testChangePassword_Success() throws Exception {
        String username = "testuser";
        String oldPassword = "oldpass";
        String newPassword = "newpass";

        Map<String, String> request = Map.of("username", username, "oldPassword", oldPassword, "newPassword", newPassword);
        String jsonRequest = "{\"username\":\"" + username + "\", \"oldPassword\":\"" + oldPassword + "\", \"newPassword\":\"" + newPassword + "\"}";

        doNothing().when(userService).changePassword(username, oldPassword, newPassword);

        mockMvc.perform(put("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(userService, times(1)).changePassword(username, oldPassword, newPassword);
    }

    @Test
    public void testChangePassword_InvalidOldPassword() throws Exception {
        String username = "testuser";
        String oldPassword = "wrongoldpass";
        String newPassword = "newpass";

        Map<String, String> request = Map.of("username", username, "oldPassword", oldPassword, "newPassword", newPassword);
        String jsonRequest = "{\"username\":\"" + username + "\", \"oldPassword\":\"" + oldPassword + "\", \"newPassword\":\"" + newPassword + "\"}";

        doThrow(new AuthenticationException("Invalid old password") {}).when(userService).changePassword(username, oldPassword, newPassword);

        mockMvc.perform(put("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid old password."))
                .andExpect(jsonPath("$.status").value(401));

        verify(userService, times(1)).changePassword(username, oldPassword, newPassword);
    }
}
