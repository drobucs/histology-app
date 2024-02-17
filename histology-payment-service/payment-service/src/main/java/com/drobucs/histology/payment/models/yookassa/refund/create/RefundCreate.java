package com.drobucs.histology.payment.models.yookassa.refund.create;

import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.payment.Deal;
import com.drobucs.histology.payment.models.yookassa.payment.create.Receipt;
import com.drobucs.histology.payment.models.yookassa.refund.Source;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundCreate {
    private String paymentId;
    private Amount amount;
    private String description;
    private Receipt receipt;
    private Source[] sources;
    private Deal deal;
}
