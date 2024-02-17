package com.drobucs.histology.payment.repositories;

import com.drobucs.histology.payment.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByNameSubscription(String nameSubscription);
    Subscription findSubscriptionById(long id);
}
