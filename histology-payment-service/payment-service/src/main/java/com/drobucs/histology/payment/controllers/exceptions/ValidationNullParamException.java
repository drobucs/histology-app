package com.drobucs.histology.payment.controllers.exceptions;

import java.util.Objects;

public class ValidationNullParamException extends Exception {
    public ValidationNullParamException(Object msg) {
        super(Objects.toString(msg));
    }
}
