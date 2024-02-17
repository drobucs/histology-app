package com.drobucs.histology.payment.services;

import com.drobucs.base.web.request.Request;
import com.drobucs.base.web.request.RequestPost;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.payment.models.Subscription;
import com.drobucs.histology.payment.network.NetworkUsersService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SubscriptionService subscriptionService;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    @Value("${backend.key.users}")
    private String backendKey;
    public int issueSubscription(long userId, long subscriptionId) {
        Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId);
        if (subscription == null) {
            logger.info("No subscription with such id='" + subscriptionId + "'.");
            return -3;
        }
        Request request = new RequestPost(NetworkUsersService.queryIssueSubscriptionToUser(backendKey))
                .setParams("userId", Long.toString(userId))
                .setParams("month", Long.toString(subscription.getNumberOfMonth()));
        RequestResult<Integer> result = request.executeInteger();
        if (result.haveErrors()) {
            logger.info("Error to issue subscription: " + result.getMessage());
            return -1;
        }
        if (result.getResult() == null) {
            logger.info("No errors but result is null.");
            return -2;
        }
        return result.getResult();
    }
}
