package com.drobucs.histology.users.auth.repositories;

import com.drobucs.histology.users.auth.models.BackendApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackendApiKeyRepository extends JpaRepository<BackendApiKey, Long> {
    BackendApiKey getBackendApiKeyByNameKey(String nameKey);
}