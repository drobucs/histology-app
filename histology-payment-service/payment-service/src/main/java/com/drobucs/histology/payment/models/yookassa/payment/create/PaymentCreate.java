package com.drobucs.histology.payment.models.yookassa.payment.create;

import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.payment.Deal;
import com.drobucs.histology.payment.models.yookassa.payment.Transfer;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreate {
    private Amount amount;
    private String description;
    private Receipt receipt;
    private Recipient recipient;
    private String paymentToken;
    private String paymentMethodId;
    private PaymentMethodData paymentMethodData;
    private Confirmation confirmation;
    private Boolean savePaymentMethod;
    private Boolean capture;
    private String clientIp;
    private Map<String, String> metadata;
    private Transfer[] transfers;
    private Deal deal;
    private FraudData fraudData;
    private String merchantCustomerId;
}
