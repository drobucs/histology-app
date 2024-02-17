package com.drobucs.histology.payment.models.yookassa.payment.create;

import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Settlement {
    private String type;
    private Amount amount;
}
