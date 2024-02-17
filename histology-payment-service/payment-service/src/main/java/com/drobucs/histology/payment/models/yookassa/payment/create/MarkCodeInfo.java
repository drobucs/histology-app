package com.drobucs.histology.payment.models.yookassa.payment.create;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarkCodeInfo {
    private String markCodeRaw;
    private String unknown;
    private String ean8;
    private String ean13;
    private String itf14;
    private String gs10;
    private String gs1m;
    private String short_;
    private String fur;
    private String egais20;
    private String egais30;
}
