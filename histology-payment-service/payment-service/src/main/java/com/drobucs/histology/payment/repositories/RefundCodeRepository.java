package com.drobucs.histology.payment.repositories;


import com.drobucs.histology.payment.models.RefundCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundCodeRepository extends JpaRepository<RefundCode, Long> {
    RefundCode getRefundCodeByRefundCodeName(String name);
}
