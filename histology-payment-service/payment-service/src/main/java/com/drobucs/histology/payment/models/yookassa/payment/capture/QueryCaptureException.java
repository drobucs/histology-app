package com.drobucs.histology.payment.models.yookassa.payment.capture;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class QueryCaptureException extends Exception {
    public QueryCaptureException(@Nullable Object msg) {
        super(Objects.toString(msg));
    }
}
