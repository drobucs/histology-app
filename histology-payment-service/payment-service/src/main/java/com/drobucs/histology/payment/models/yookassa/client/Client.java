package com.drobucs.histology.payment.models.yookassa.client;

import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class Client {
    private Client() {}

    private static OkHttpClient client;

    public synchronized static OkHttpClient getClient(@NotNull String shopId, @NotNull String secretKey) {
        if (client == null) {
            client = com.drobucs.base.web.client.Client.newAuthenticationClient(shopId, secretKey);
        }
        return client;
    }
}
