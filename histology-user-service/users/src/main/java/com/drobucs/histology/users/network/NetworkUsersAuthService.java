package com.drobucs.histology.users.network;

import org.jetbrains.annotations.NotNull;

public class NetworkUsersAuthService {
    private static final String HOST = "http://172.17.0.1";
    private static final long PORT = 8083;
    private static final String API = "/api/backend/users/auth";
    private static final String API_VERSION = "1";

    @NotNull
    private static String generalHost() {
        return HOST;
    }

    @NotNull
    private static String host() {
        return generalHost() + ":" + PORT;
    }
    @NotNull
    private static String api() {
        return host() + API + "/" + API_VERSION;
    }
    @NotNull
    public static String queryUserExist(@NotNull String apiKey, @NotNull String login) {
        return api() + "/" + apiKey+ "/user/exist/" + login;
    }
}
