package com.drobucs.histology.payment.models.yookassa.client;

import java.util.Objects;

public class CannotCreateClientException extends Exception {
    public CannotCreateClientException(Object msg) {
        super(Objects.toString(msg));
    }
}
