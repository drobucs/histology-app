package com.drobucs.histology.payment.services;

import com.drobucs.histology.payment.models.Subscription;
import com.drobucs.histology.payment.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    @Nullable
    public Subscription getSubscriptionByName(@NotNull String name) {
        return subscriptionRepository.findByNameSubscription(name);
    }

    @NotNull
    public List<Subscription> getSubscriptionsList() {
        return subscriptionRepository.findAll();
    }

    public Subscription getSubscriptionById(long id) {
        return subscriptionRepository.findSubscriptionById(id);
    }
}
