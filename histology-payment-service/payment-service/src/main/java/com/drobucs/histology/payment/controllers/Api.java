package com.drobucs.histology.payment.controllers;

import com.drobucs.base.web.request.RequestGet;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.payment.controllers.exceptions.ValidationNullParamException;
import com.drobucs.histology.payment.network.NetworkUsersService;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class Api {
    private static final boolean LOG_ENABLED = true;

    @Value("${backend.key.users}")
    protected String backendApiKey;
    private final Logger logger = LoggerFactory.getLogger(Api.class);
    protected void logInfo(@Nullable String msg) {
        if (LOG_ENABLED) {
            logger.info(msg);
        }
    }

    protected boolean isBadApiKey(@Nullable String login, @Nullable String apiKey) {
        if (login == null || apiKey == null) {
            if (login == null) {
                logInfo("isBadApiKey: Login is null.");
            } else {
                logInfo("isBadApiKey: apiKey is null.");
            }
            return true;
        }
        String query = NetworkUsersService.queryCheckUsersApiKey(backendApiKey, login, apiKey);
        RequestGet requestGet = new RequestGet(query);
        RequestResult<Integer> res = requestGet.executeInteger();
        if (res.haveErrors()) {
            logInfo("isBadApiKey: Request result have errors: " + res.getMessage());
            logInfo("isBadApiKey:RequestGet:" + query);
            logInfo("isBadApiKey:login='" + login + "',apiKey='" + apiKey + "'");
            logInfo("isBadApiKey:backendApiKey='" + backendApiKey + "'");
            logInfo("isBadApiKey:res='" + res + "'");
            return true;
        }
        if (res.getResult() == null) {
            logInfo("isBadApiKey: No errors but result is null.");
            logInfo("isBadApiKey:RequestGet:" + query);
            logInfo("isBadApiKey:login='" + login + "',apiKey='" + apiKey + "'");
            logInfo("isBadApiKey:backendApiKey='" + backendApiKey + "'");
            logInfo("isBadApiKey:res='" + res + "'");
            return true;
        }
        if (res.getResult() != 0) {
            logInfo("isBadApiKey:RequestGet:" + query);
            logInfo("isBadApiKey:login='" + login + "',apiKey='" + apiKey + "'");
            logInfo("isBadApiKey:backendApiKey='" + backendApiKey + "'");
            logInfo("isBadApiKey:res='" + res + "'");
        }
        return res.getResult() != 0;
    }

    protected String[] names(String... names) {
        return names;
    }

    protected void notNullValidation(String[] names, Object... objects) throws ValidationNullParamException {
        if (names.length != objects.length) {
            throw new ValidationNullParamException("Server error: params.size() != names.size()");
        }
        for (int i = 0; i < names.length; ++i) {
            if (objects[i] == null) {
                throw new ValidationNullParamException("Null error: " + names[i] + " is null.");
            }
        }
    }
}
