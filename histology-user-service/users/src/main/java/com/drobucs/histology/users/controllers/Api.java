package com.drobucs.histology.users.controllers;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Api {
    private final Logger logger = LoggerFactory.getLogger(Api.class);
    protected void logInfo(@NonNull String message) {
        logger.info(message);
    }

    protected void logWarn(@NonNull String message) {
        logger.warn(message);
    }
    protected void logError(@NonNull String message) {
        logger.error(message);
    }
}
