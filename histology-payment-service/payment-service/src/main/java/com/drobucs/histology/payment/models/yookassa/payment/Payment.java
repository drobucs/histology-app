package com.drobucs.histology.payment.models.yookassa.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String id;
    private String status;
    private Amount amount;
    private Amount incomeAmount;
    private String description;
    private Recipient recipient;
    private PaymentMethod paymentMethod;
    private String capturedAt;
    private String createdAt;
    private String expiresAt;
    private Confirmation confirmation;
    private Boolean test;
    private Amount refundedAmount;
    private Boolean paid;
    private Boolean refundable;
    private String receiptRegistration;
    private Map<String, String> metadata;
    private CancellationDetails cancellationDetails;
    private AuthorizationDetails authorizationDetails;
    private Transfer[] transfers;
    private Deal deal;
    private String merchantCustomerId;
}
