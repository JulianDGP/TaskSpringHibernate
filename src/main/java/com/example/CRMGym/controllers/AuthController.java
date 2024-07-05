package com.example.CRMGym.controllers;

import com.example.CRMGym.exceptions.ErrorResponse;
import com.example.CRMGym.security.JwtTokenProvider;
import com.example.CRMGym.services.TokenBlacklist;
import com.example.CRMGym.services.UserService;
import com.example.CRMGym.services.implementations.LoginAttemptServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final LoginAttemptServiceImpl loginAttemptService;
    private final TokenBlacklist tokenBlacklist;



    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, LoginAttemptServiceImpl loginAttemptService, TokenBlacklist tokenBlacklist) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklist = tokenBlacklist;
    }

    /* 3.Login with GET method */
    @Operation(summary = "Login", description = "User login")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        log.info("Attempting to login user: {}", username);

        if (loginAttemptService.isBlocked(username)) {
            log.warn("User {} is blocked due to too many failed login attempts", username);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse("Too many failed login attempts. Please try again later.", HttpStatus.TOO_MANY_REQUESTS.value()));
        }

        try {
            // Autentica al usuario usando el AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Genera un token JWT para el usuario autenticado
            String token = jwtTokenProvider.createToken(username);
            log.info("User {} authenticated successfully. Token generated.", username);
            // Retorna el token en la respuesta
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            log.error("Failed login attempt for user: {}", username, e);
            loginAttemptService.loginFailed(username);
            // Retorna un error 401 si las credenciales son inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials.", HttpStatus.UNAUTHORIZED.value()));
        }
    }


    /* 4. Change Login password with PUT */
    @Operation(summary = "Change Password", description = "Change user password", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        log.info("Attempting to change password for user: {}", username);
        try {
            // Authenticate with old password
            authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(username, oldPassword));
            userService.changePassword(username, oldPassword, newPassword);
            log.info("Password changed successfully for user: {}", username);
            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            log.error("Failed to change password for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid old password.", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            tokenBlacklist.addToBlacklist(token);
            log.info("Token added to blacklist: {}", token);
        } else {
            log.warn("No token found in the request for logout");
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
