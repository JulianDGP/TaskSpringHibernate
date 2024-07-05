package com.example.CRMGym.services;

public interface LoginAttemptService {
    void loginFailed(String username);
    boolean isBlocked(String username);
}
