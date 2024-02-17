package com.drobucs.histology.users.auth.services;

import com.drobucs.base.web.request.Request;
import com.drobucs.base.web.request.RequestPost;
import com.drobucs.histology.users.auth.network.NetworkStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatService {
    private final Logger logger = LoggerFactory.getLogger(StatService.class);

    public void eventRegister(final Long userId) {
        new Thread(() -> {
            if (userId != null) {
                Request request = new RequestPost(NetworkStat.eventRegister())
                        .setParams("userId", userId.toString());
                request.executeInteger();
            } else {
                logger.error("RegisterEvent: userId is null.");
            }
        }).start();
    }
}
