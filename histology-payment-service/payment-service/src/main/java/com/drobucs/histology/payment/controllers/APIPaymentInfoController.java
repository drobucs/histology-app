package com.drobucs.histology.payment.controllers;

import com.drobucs.histology.payment.models.Subscription;
import com.drobucs.histology.payment.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment/1/{login}/{apiKey}/info")
@RequiredArgsConstructor
public class APIPaymentInfoController extends Api {
    private final SubscriptionService subscriptionService;

    @GetMapping("/test")
    public String test(@PathVariable String apiKey, @PathVariable String login) {
        // params ignored
        return "API is alive.";
    }

    @GetMapping("/subscription/{nameSubscription}")
    public Subscription getSubscriptionFirstPlan(@PathVariable String apiKey,
                                                               @PathVariable String login,
                                                               @PathVariable String nameSubscription) {
        if (isBadApiKey(login, apiKey)) {
            logInfo("Bad api key.");
            return null;
        }
        Subscription subscription = subscriptionService.getSubscriptionByName(nameSubscription);
        if (subscription == null) {
            logInfo("No such subscription.");
            return null;
        }
        return subscription;
    }

    @GetMapping("/list/subscriptions")
    public List<Subscription> getSubscriptionsList(@PathVariable String apiKey, @PathVariable String login) {
        if (isBadApiKey(login, apiKey)) {
            logInfo("Bad api key.");
            return null;
        }
        return subscriptionService.getSubscriptionsList();
    }
}
