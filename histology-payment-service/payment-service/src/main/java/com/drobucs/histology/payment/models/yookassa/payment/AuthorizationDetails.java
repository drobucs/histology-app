package com.drobucs.histology.payment.models.yookassa.payment;

import com.drobucs.histology.payment.models.yookassa.payment.create.ThreeDSecure;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationDetails {
    private String rrn;
    private String authCode;
    @JsonProperty("three_d_secure")
    private ThreeDSecure threeDSecure;
}
