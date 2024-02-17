package com.drobucs.histology.payment.models.yookassa.exceptions;

import java.util.Objects;

public class StatusInfoException extends Exception {
    public StatusInfoException(Object msg) {
        super(Objects.toString(msg));
    }
}
