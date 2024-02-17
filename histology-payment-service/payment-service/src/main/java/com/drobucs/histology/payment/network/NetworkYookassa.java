package com.drobucs.histology.payment.network;

import org.jetbrains.annotations.NotNull;

public class NetworkYookassa {
    private static final String HOST = "https://api.yookassa.ru/v3";

    @NotNull
    private static String generalHost() {
        return HOST;
    }

    @NotNull
    private static String host() {
        return generalHost();
    }
    @NotNull
    private static String api() {
        return host();
    }
    @NotNull
    public static String queryCreatePayment() {
        return api()  + "/payments";
    }

    @NotNull
    public static String queryListPayments() {
        return api()  + "/payments";
    }

    @NotNull
    public static String queryPaymentInfo(@NotNull String paymentId) {
        return api()  + "/payments/" + paymentId;
    }

    @NotNull
    public static String queryPaymentCapture(@NotNull String paymentId) {
        return api()  + "/payments/" + paymentId + "/capture";
    }

    @NotNull
    public static String queryPaymentCancel(@NotNull String paymentId) {
        return api()  + "/payments/" + paymentId + "/cancel";
    }

    @NotNull
    public static String queryCreateRefund() {
        return api() + "/refunds";
    }
}
