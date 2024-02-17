package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findPaymentByUserId(long userId);
    Payment findFirstByUserIdOrderByPaymentTimeDesc(long userId);

    Payment findPaymentByPaymentId(String paymentId);
}
