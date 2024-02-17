package com.drobucs.histology.payment.models.notifications;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NotificationEvent {
    public static final String REFUND_SUCCESS = "refund.succeeded";
    public static final String PAYMENT_SUCCEEDED = "payment.succeeded";
    public static final String PAYMENT_CANCELED = "payment.canceled";
    public static final String PAYMENT_WAITING_FOR_CAPTURE = "payment.waiting_for_capture";
    private static final String REFUND = "refund";
    public static boolean isRefundEvent(@NotNull String notificationType) {
        return notificationType.startsWith(REFUND);
    }

    public static boolean isPaymentSucceeded(@NotNull String notificationType) {
        return Objects.equals(notificationType, PAYMENT_SUCCEEDED);
    }

    public static boolean isPaymentCanceled(@NotNull String notificationType) {
        return Objects.equals(notificationType, PAYMENT_CANCELED);
    }

    public static boolean isPaymentWaitingForCapture(@NotNull String notificationType) {
        return Objects.equals(notificationType, PAYMENT_WAITING_FOR_CAPTURE);
    }
}
