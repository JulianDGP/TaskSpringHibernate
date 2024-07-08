package com.example.CRMGym.services.implementations;

import com.example.CRMGym.services.DenyTokensList;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DenyTokensListImpl implements DenyTokensList {
    private final Set<String> denyTokensList  = new HashSet<>();
    @Override
    public void addToDenylist(String token) {
        denyTokensList.add(token);
    }

    @Override
    public boolean isDenylisted(String token) {
        return denyTokensList.contains(token);
    }
}
