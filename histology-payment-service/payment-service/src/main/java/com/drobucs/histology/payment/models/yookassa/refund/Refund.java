package com.drobucs.histology.payment.models.yookassa.refund;

import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.payment.CancellationDetails;
import com.drobucs.histology.payment.models.yookassa.payment.Deal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Refund {
    private String id;
    private String paymentId;
    private String status;
    private CancellationDetails cancellationDetails;
    private String receiptRegistration;
    private String createdAt;
    private Amount amount;
    private String description;
    private Source[] sources;
    private Deal deal;
}
