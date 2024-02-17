package com.drobucs.histology.users.repositories;

import com.drobucs.histology.users.models.BackendKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackendKeyRepository extends JpaRepository<BackendKey, Long> {
    BackendKey findBackendKeyByNameKey(String name);
}
