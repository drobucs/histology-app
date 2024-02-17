package com.drobucs.histology.payment.controllers.exceptions;

import java.util.Objects;

public class CreateRequestException extends Exception {
    public CreateRequestException(Object msg) {
        super(Objects.toString(msg));
    }
}
