package com.drobucs.histology.payment.models;

import com.drobucs.histology.payment.models.yookassa.exceptions.UnknownPaymentStatusException;
import com.drobucs.histology.payment.models.yookassa.payment.CancellationDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatus {
    public static final String SUCCESS = "SUCCESS";
    public static final String WAITING_FOR_CAPTURE = "WAITING_FOR_CAPTURE";
    public static final String CANCELED = "CANCELED";
    public static final String PENDING = "PENDING";
    public static final String SUCCESS_BUT_ERROR = "SUCCESS_BUT_ERROR";
    public static final String REFUNDED = "REFUNDED";
    public static final String UNKNOWN = "UNKNOWN";
    @NotNull
    public static String convertFromYookassa(@NotNull String yookassaStatusString) throws UnknownPaymentStatusException {
        if (yookassaStatusString.equals("succeeded")) {
            return SUCCESS;
        } else if (yookassaStatusString.equals("pending")) {
            return PENDING;
        } else if (yookassaStatusString.equals("waiting_for_capture")) {
            return WAITING_FOR_CAPTURE;
        } else if (yookassaStatusString.equals("canceled")) {
            return CANCELED;
        }
        throw new UnknownPaymentStatusException("Unknown payment status: yookassaStatusString='" + yookassaStatusString + "'.");
    }

    @NotNull
    public static String convertToYookassa(@NotNull String statusString) throws UnknownPaymentStatusException {
        if (statusString.equals(SUCCESS)) {
            return "succeeded";
        } else if (statusString.equals(PENDING)) {
            return "pending";
        } else if (statusString.equals(WAITING_FOR_CAPTURE)) {
            return "waiting_for_capture";
        } else if (statusString.equals(CANCELED)) {
            return "canceled";
        }
        throw new UnknownPaymentStatusException("Unknown payment status: statusString='" + statusString + "'.");
    }
    private String status;
    private String message;
    private CancellationDetails cancellationDetails;
}
