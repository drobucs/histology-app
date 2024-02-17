package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
}
