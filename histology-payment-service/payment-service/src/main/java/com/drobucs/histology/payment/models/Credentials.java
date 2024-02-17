package com.drobucs.histology.payment.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    public static final Credentials ERROR = new Credentials(null, null, null);
    private String shopId;
    private String clientApplicationKey;
    private String authCenterClientId;
}
