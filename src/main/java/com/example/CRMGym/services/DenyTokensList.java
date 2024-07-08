package com.example.CRMGym.services;


public interface DenyTokensList {
    void addToDenylist(String token);
    boolean isDenylisted(String token);
}
