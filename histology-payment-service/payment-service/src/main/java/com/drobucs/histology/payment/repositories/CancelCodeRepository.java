package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.CancelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelCodeRepository extends JpaRepository<CancelCode, Long> {
    CancelCode getCancelCodeByCancelCodeName(String name);
}
