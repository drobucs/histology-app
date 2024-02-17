package com.drobucs.histology.users.services;

import com.drobucs.histology.users.models.BackendKey;
import com.drobucs.histology.users.repositories.BackendKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackendKeyService {
    private final BackendKeyRepository backendKeyRepository;
    public String getKeyByName(String name) {
        BackendKey backendKey = backendKeyRepository.findBackendKeyByNameKey(name);
        if (backendKey == null) {
            return null;
        }
        return backendKey.getApiKey();
    }
}
