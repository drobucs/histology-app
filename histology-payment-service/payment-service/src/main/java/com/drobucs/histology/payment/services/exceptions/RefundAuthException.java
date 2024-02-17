package com.drobucs.histology.payment.services.exceptions;

import java.util.Objects;

public class RefundAuthException extends Exception {
    public RefundAuthException(Object msg) {
        super(Objects.toString(msg));
    }
}
