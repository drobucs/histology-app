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
public class Receipt {
    private Customer customer;
    private Item[] items;
    private String phone;
    private String email;
    private Long taxSystemCode;
    private IndustryDetails[] receiptIndustryDetails;
    private OperationalDetails[] receiptOperationalDetails;
}
