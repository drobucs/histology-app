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
public class Item {
    private String description;
    private Amount amount;
    private Long vatCode;
    private String quantity;
    private String measure;
    private MarkQuantity markQuantity;
    private String paymentSubject;
    private String paymentMode;
    private String countryOfOriginCode;
    private String customsDeclarationNumber;
    private String excise;
    private String productCode;
    private MarkCodeInfo markCodeInfo;
    private String markMode;
    private IndustryDetails paymentSubjectIndustryDetails;
}
