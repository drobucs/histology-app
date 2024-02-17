package com.drobucs.histology.payment.models.yookassa.exceptions;

import java.util.Objects;

public class UnknownPaymentStatusException extends Exception {
    public UnknownPaymentStatusException(Object msg) {
        super(Objects.toString(msg));
    }
}
