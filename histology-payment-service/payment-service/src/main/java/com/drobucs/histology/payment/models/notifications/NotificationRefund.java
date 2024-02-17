package com.drobucs.histology.payment.models.notifications;

import com.drobucs.histology.payment.models.yookassa.refund.Refund;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRefund extends Notification {
    protected Refund object;

    @Override
    public Object getObject() {
        return object;
    }
}
