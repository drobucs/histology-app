package com.drobucs.histology.payment.models.yookassa.payment.capture;

import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.payment.Deal;
import com.drobucs.histology.payment.models.yookassa.payment.Transfer;
import com.drobucs.histology.payment.models.yookassa.payment.create.Receipt;
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
public class Capture {
    private Amount amount;
    private Receipt receipt;
    private Transfer[] transfers;
    private Deal deal;
}
