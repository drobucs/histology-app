package com.drobucs.histology.users.auth.services;

import com.drobucs.histology.users.auth.repositories.BackendApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackendApiKeyService {
    private final BackendApiKeyRepository backendApiKeyRepository;
    public String getKeyByName(String name) {
        return backendApiKeyRepository.getBackendApiKeyByNameKey(name).getApiKey();
    }
}
