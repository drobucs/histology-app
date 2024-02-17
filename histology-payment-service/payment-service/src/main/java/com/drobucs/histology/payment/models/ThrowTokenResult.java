package com.drobucs.histology.payment.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ThrowTokenResult {
    private final String confirmationUrl;
    private final String message;
    private final boolean error;
    private final String lastPaymentId;
}
