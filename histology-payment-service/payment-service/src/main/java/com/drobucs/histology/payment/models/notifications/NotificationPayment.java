package com.drobucs.histology.payment.models.notifications;

import com.drobucs.histology.payment.models.yookassa.payment.Payment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPayment extends Notification {
    protected Payment object;

    @Override
    public Object getObject() {
        return object;
    }
}
