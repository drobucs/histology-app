package com.drobucs.histology.users.services;

import com.drobucs.base.web.request.RequestPost;
import com.drobucs.base.web.utils.notifier.Notifier;
import com.drobucs.base.web.utils.notifier.NotifierFactory;
import com.drobucs.histology.users.models.User;
import com.drobucs.histology.users.network.NetworkBotService;
import com.drobucs.histology.users.network.NetworkStatService;
import com.drobucs.histology.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatService {
    private final Logger logger = LoggerFactory.getLogger(StatService.class);
    private final Notifier notifier = NotifierFactory.getNotifier(logger);
    private final UserRepository userRepository;

    public void enter(Long userId, String status) {
        new Thread(() -> {
            if (userId == null) {
                logger.info("userId is null.");
                return;
            }
            if (status == null) {
                logger.info("status is null.");
                return;
            }
            var res = new RequestPost(NetworkStatService.queryEventEnter())
                    .setParams("userId", userId.toString())
                    .setParams("status", status)
                    .executeInteger();
            if (res.haveErrors()) {
                logger.info(res.getMessage());
            }
        }).start();
    }

    public void register(final String login) {
        new Thread(() -> {
            if (login == null) {
                logger.info("login is null.");
                return;
            }
            User user = userRepository.getUserByLogin(login);
            if (user == null) {
                logger.info("cannot find user with this login.");
                return;
            }
            new RequestPost(NetworkStatService.queryEventRegister())
                    .setParams("userId", user.getId().toString())
                    .executeInteger(); // ignore result
            notifier.notifyGet(NetworkBotService.queryNewUser());
        }).start();
    }
}
