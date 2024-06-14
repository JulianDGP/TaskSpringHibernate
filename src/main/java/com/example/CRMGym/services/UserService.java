package com.example.CRMGym.services;

public interface UserService {
    void changePassword(String username, String oldPassword, String newPassword);
}
